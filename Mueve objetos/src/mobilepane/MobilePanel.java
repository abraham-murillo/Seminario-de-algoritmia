package mobilepane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MobilePanel extends JFrame {
    private JButton Animar;
    private JPanel panelArea;
    public JPanel ventanaPrincipal;

    int x = 0;
    int y = 0;

    public MobilePanel() {
        insertPanel();
        Animar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animatePanel();
                System.out.println("Panel animado, se mueve muy raro");
            }
        });
    }

    public void animatePanel(){
        JPanel panel = (JPanel) panelArea.getComponent(0);
        Dimension size = panel.getPreferredSize();

        panel.setBounds(x, y, size.width, size.height);
        panel.repaint();

        x += 20;
        x %= panelArea.getWidth();

        y += 20;
        y %= panelArea.getHeight();
    }

    public void insertPanel(){
        JPanel panel = new JPanel();
        JLabel label = new JLabel("aaa");
        panel.add(label);
        panel.setBackground(Color.red);
        panel.setPreferredSize(new Dimension(50,50));

        Dimension size = panel.getPreferredSize();
        panel.setBounds(100, 100, size.width, size.height);
        panelArea.add(panel);
    }

}
