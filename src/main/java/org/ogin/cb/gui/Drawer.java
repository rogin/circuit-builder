package org.ogin.cb.gui;

import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.JPanel;

public class Drawer extends JPanel {

    private static final long serialVersionUID = -6861319042385297815L;

    public Drawer() {
        init();
    }

    private void init() {
        /*
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        
        //BoxLayout layout = new BoxLayout(this, BoxLayout.LINE_AXIS);
        //Dimension fixedwidth = new Dimension(15, 0);
        //Dimension infinitewidth = new Dimension(Short.MAX_VALUE, 0);
        //Box.Filler filler = new Box.Filler(fixedwidth, fixedwidth, infinitewidth);
        //setLayout(layout);
        //add(Box.createHorizontalGlue());
        add(new OrGateOption(), c);

        c.gridx = 1;
        //add(Box.createRigidArea(new Dimension(10, 0)));
        add(new NorGateOption(), c);
        */

        //add(new OrGateOption());
        //add(new NorGateOption());
        
        //BaseOption[] options = new BaseOption[] {new OrGateOption(), new NorGateOption()};
        //JList<BaseOption> list = new JList<BaseOption>(options);
        
        JList<String> list = new JList<String>(new String[] {"OR", "NOR"});
        list.setDragEnabled(true);
        list.setDropMode(DropMode.ON);
        add(list);
    }
}