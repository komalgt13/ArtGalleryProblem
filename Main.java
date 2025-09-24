// GroupID-19 (Komal 22113078_Dhruv 22114029_Himanshu Raheja22323023)
// Date: September 24, 2025
// Main.java: Main orchestrator file giving the user the choice to choose between Art Gallery problem and Camera Placement Problem

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Computational Geometry Project Launcher");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            frame.setSize(600, 220); 
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
            mainPanel.setBackground(new Color(245, 245, 245));

            JLabel titleLabel = new JLabel("Select a Problem to Visualize", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            mainPanel.add(titleLabel, BorderLayout.NORTH);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridLayout(2, 1, 10, 10));
            buttonPanel.setBackground(mainPanel.getBackground());

            JButton artGalleryButton = new JButton("Classic Art Gallery Problem (360° Vertex Guards)");
            artGalleryButton.setFont(new Font("Arial", Font.PLAIN, 14));
            artGalleryButton.setToolTipText("Solves for the minimum number of guards with full 360-degree vision.");
            artGalleryButton.addActionListener(e -> {
                new ArtGalleryGUI().setVisible(true);
                frame.dispose();
            });

            JButton cameraPlacementButton = new JButton("Camera Placement Problem (50° Field of View)");
            cameraPlacementButton.setFont(new Font("Arial", Font.PLAIN, 14));
            cameraPlacementButton.setToolTipText("Solves for optimal camera placement using a fixed 50-degree field of view.");
            cameraPlacementButton.addActionListener(e -> {
                new CameraPlacementGUI().setVisible(true);
                frame.dispose();
            });

            buttonPanel.add(artGalleryButton);
            buttonPanel.add(cameraPlacementButton);
            mainPanel.add(buttonPanel, BorderLayout.CENTER);

            frame.add(mainPanel);
            frame.setVisible(true);
        });
    }
}

