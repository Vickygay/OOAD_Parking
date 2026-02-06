package views;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// TO-DO for admin:
// 1. to display the available space for each customer to see,
// 2. and then have entry button to let customer fill up details
public class home extends JFrame{

    private Color blueColor = new Color(3, 78, 161);
    private Color redColor = new Color(255, 7, 7);
    private Color whiteGreyColor = new Color(238,241,241);
    private Font headerFont = new Font("SansSerif", Font.BOLD, 30);
    private Font contentFont = new Font("SansSerif", Font.BOLD, 20);

    public home()
    {
        setTitle("Home");
        setSize(1300, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(blueColor);
        JLabel title = new JLabel("Available Space");
        title.setForeground(Color.WHITE);
        title.setFont(headerFont);
        header.add(title);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(whiteGreyColor);

        // have two buttons here
        JButton entry = new JButton("Entry");
        JButton back = new JButton("Back"); 

        entry.setPreferredSize(new Dimension(100, 50));
        back.setPreferredSize(new Dimension(100, 50));

        // put text under image
        entry.setVerticalTextPosition(SwingConstants.BOTTOM);
        entry.setHorizontalTextPosition(SwingConstants.CENTER);

        back.setVerticalTextPosition(SwingConstants.BOTTOM);
        back.setHorizontalTextPosition(SwingConstants.CENTER);

        // set font for the text in the button
        entry.setFont(contentFont);
        back.setFont(contentFont);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 40, 0, 40);

        entry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                new entry().setVisible(true);
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

        content.add(entry, gbc);
        content.add(back, gbc);

        add(content, BorderLayout.CENTER);
    }
}
