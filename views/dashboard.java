package views;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class dashboard extends JFrame{
    private Color blueColor = new Color(3, 78, 161);
    private Color redColor = new Color(255, 7, 7);
    private Color whiteGreyColor = new Color(238,241,241);
    private Font headerFont = new Font("SansSerif", Font.BOLD, 30);
    private Font contentFont = new Font("SansSerif", Font.BOLD, 20);
        
    public dashboard()
    {
        setTitle("Parking Lot Management System ");
        setSize(1300, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(blueColor);
        JLabel title = new JLabel("Welcome to Parking Lot Management System");
        title.setForeground(Color.WHITE);
        title.setFont(headerFont);
        header.add(title);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(whiteGreyColor);

        // get the icon
        ImageIcon entryIcon = new ImageIcon("images/parking-car.png");
        ImageIcon exitIcon  = new ImageIcon("images/exit.png");

        // scale the entry icon make it smaller
        Image img1 = entryIcon.getImage();
        Image resizedImg = img1.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        ImageIcon scaledEntryIcon = new ImageIcon(resizedImg);
         

        // scale the exit icon make it smaller
        Image img2 = exitIcon.getImage();
        Image resizedImg2 = img2.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        ImageIcon scaledExitIcon = new ImageIcon(resizedImg2);

        // have two buttons here
        JButton entry = new JButton("Entry", scaledEntryIcon);
        JButton exit = new JButton("Exit", scaledExitIcon); 

        entry.setPreferredSize(new Dimension(220, 220));
        exit.setPreferredSize(new Dimension(220, 220));

        // put text under image
        entry.setVerticalTextPosition(SwingConstants.BOTTOM);
        entry.setHorizontalTextPosition(SwingConstants.CENTER);

        exit.setVerticalTextPosition(SwingConstants.BOTTOM);
        exit.setHorizontalTextPosition(SwingConstants.CENTER);

        // set font for the text in the button
        entry.setFont(contentFont);
        exit.setFont(contentFont);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 40, 0, 40);

        content.add(entry, gbc);
        content.add(exit, gbc);

        add(content, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 20));
        JButton logIn = new JButton("Log In as Admin");
        logIn.setSize(40, 50);        
        logIn.setFont(contentFont);

        logIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                new adminLogin().setVisible(true);
                dispose();
            }
        });

        footer.add(logIn, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);



    }
    
}
