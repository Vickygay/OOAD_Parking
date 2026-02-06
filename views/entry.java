package views;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class entry extends JFrame{
    private Color blueColor = new Color(3, 78, 161);
    private Color redColor = new Color(255, 7, 7);
    private Color whiteGreyColor = new Color(238,241,241);
    private Font headerFont = new Font("SansSerif", Font.BOLD, 30);
    private Font contentFont = new Font("SansSerif", Font.BOLD, 20);

    public entry()
    {
        setTitle("Entry");
        setSize(1300, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        JPanel header = new JPanel();
        header.setBackground(blueColor);
        JLabel title = new JLabel("Please fill up the details to secure a spot");
        title.setForeground(Color.WHITE);
        title.setFont(headerFont);
        header.add(title);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(whiteGreyColor);

        // have two buttons here
        JButton submit = new JButton("Submit");
        JButton back = new JButton("Back"); 

        submit.setPreferredSize(new Dimension(150, 50));
        back.setPreferredSize(new Dimension(100, 50));

        // put text under image
        submit.setVerticalTextPosition(SwingConstants.BOTTOM);
        submit.setHorizontalTextPosition(SwingConstants.CENTER);

        back.setVerticalTextPosition(SwingConstants.BOTTOM);
        back.setHorizontalTextPosition(SwingConstants.CENTER);

        // set font for the text in the button
        submit.setFont(contentFont);
        back.setFont(contentFont);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 40, 0, 40);

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JOptionPane.showMessageDialog(null, "Submitted Successfully! ");

                new dashboard().setVisible(true);
                dispose();
            }
        });

        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                new dashboard().setVisible(true);
                dispose();
            }
        });

        content.add(submit, gbc);
        content.add(back, gbc);

        add(content, BorderLayout.CENTER);
    }

}
