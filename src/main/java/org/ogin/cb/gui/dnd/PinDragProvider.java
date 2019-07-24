package org.ogin.cb.gui.dnd;

import java.awt.Color;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.function.BiConsumer;

import org.ogin.cb.gui.CircuitModel;
import org.ogin.cb.gui.components.AbstractGate;
import org.ogin.cb.gui.components.Pin;
import org.ogin.cb.gui.components.Wire;

/**
 * Configures pins for Drag-n-Drop when a gate is instantiated.
 */
public class PinDragProvider implements ContainerListener {
    private static final Color PIN_COLOR_VALID_DROP = Color.GREEN;
    private static final Color PIN_COLOR_INVALID_DROP = Color.RED;

    private CircuitModel model;

    /** provides notifications when a wire is drawn */
    private PinTransferHandler pinTransferHandler;

    /** toggles cursor during pin drag events */
    private PinDragSourceListener pinDragSourceListener;

    public PinDragProvider(CircuitModel model, BiConsumer<Pin, Pin> wireCreationCallback) {
        this.model = model;

        pinTransferHandler = new PinTransferHandler(wireCreationCallback);
        pinDragSourceListener = new PinDragSourceListener();
    }

    @Override
    public void componentAdded(ContainerEvent event) {
        if(event.getChild() instanceof AbstractGate) {
            AbstractGate gate = (AbstractGate)event.getChild();

            assignTransferHandler(gate.getInPins());
            configureDragSources(gate.getOutPins());
        } else if(event.getChild() instanceof Wire) {
            Wire wire = (Wire)event.getChild();
            
            markInPinUnavailable(wire.getInPin());
        }
    }

    @Override
    public void componentRemoved(ContainerEvent event) {
        if(event.getChild() instanceof Wire) {
            Wire wire = (Wire)event.getChild();

            // now that the wire detached from the IN pin,
            // allow it to be used as a drop target
            markInPinAvailable(wire.getInPin());
        }
    }

    private void markInPinUnavailable(Pin pin) {
        pin.getDropTarget().setActive(false);
    }

    private void markInPinAvailable(Pin pin) {
        pin.getDropTarget().setActive(true);
    }

    private void assignTransferHandler(Pin[] inPins) {
        for (Pin pin : inPins) {
            pin.setTransferHandler(pinTransferHandler);
        }
    }

    private void configureDragSources(Pin[] outPins) {
        for (Pin outPin : outPins) {
            DragSource source = new DragSource();

            source.addDragSourceListener(pinDragSourceListener);
            source.addDragSourceListener(new DragSourceAdapter() {
                @Override
                public void dragDropEnd(DragSourceDropEvent event) {
                    configureInPinsAfterDragEvent((Pin) event.getDragSourceContext().getComponent());
                }
            });
            DragGestureListener dragListener = new DragGestureListener() {

                @Override
                public void dragGestureRecognized(DragGestureEvent dragEvent) {
                    prepareInPinBackgroundsForDragEvent((Pin) dragEvent.getComponent());
                    dragEvent.startDrag(DragSource.DefaultLinkDrop,
                            new PinSelection(PinTransferHandler.PIN_FLAVOR, outPin));
                }
            };
            source.createDefaultDragGestureRecognizer(outPin, DnDConstants.ACTION_LINK, dragListener);
        }
    }

    private void configureInPinsAfterDragEvent(Pin outPinEventOriginator) {
        for (Pin pin : model.getInPins()) {
            pin.setBackground(Pin.DEFAULT_BACKGROUND_COLOR);
        }

        // all IN pins on the same gate as the OUT drag originator were marked
        // unavailable, so review and reset those that should be available.
        for (Pin pin : model.getGateOwningOutPin(outPinEventOriginator).getInPins()) {
            if (!model.isWired(pin)) {
                markInPinAvailable(pin);
            }
        }
    }

    private void prepareInPinBackgroundsForDragEvent(Pin outPinEventOriginator) {
        // all IN pins on the same gate as the OUT drag originator need to be
        // unavailable
        for (Pin pin : model.getGateOwningOutPin(outPinEventOriginator).getInPins()) {
            markInPinUnavailable(pin);
        }

        for (Pin pin : model.getInPins()) {
            Color newBackground = pin.getDropTarget().isActive() ? PIN_COLOR_VALID_DROP : PIN_COLOR_INVALID_DROP;
            pin.setBackground(newBackground);
        }
    }
}