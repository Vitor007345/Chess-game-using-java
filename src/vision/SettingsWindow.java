package vision;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import chessgame.Settings;
import services.SettingsFileManager;
import services.errors.LoadingException;
import services.errors.SavingException;

/**
 * The Graphical User Interface (GUI) window for configuring game preferences.
 * This class provides interactive checkboxes for options like board auto-rotation,
 * automatic pawn promotion, and coordinate visibility. It dynamically monitors changes
 * to toggle the visibility of the "Apply Changes" button.
 */
public class SettingsWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    
    /** Checkbox to toggle the automatic board reversal/rotation feature. */
    private JCheckBox chkAutoReverse;
    
    /** Checkbox to toggle automatic pawn promotion to a Queen. */
    private JCheckBox chkAutoPromote;
    
    /** Checkbox to toggle the visibility of board coordinates (a-h, 1-8). */
    private JCheckBox chkShowCoords;
    
    /** Button used to save the updated settings to the file system. */
    private JButton btnApply;
    
    /** The underlying settings model containing the current game configurations. */
    private Settings settings;

    /**
     * Constructs a new SettingsWindow.
     * Initializes the frame layout, loads existing preferences from disk, populates the UI options,
     * hooks up interactive action listeners to detect real-time changes, and configures navigation flow.
     */
    public SettingsWindow() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(100, 100, 550, 650);
        this.setTitle("Java Chess Engine - Settings");

        JPanel contentPane = new JPanel();
        contentPane.setBackground(new Color(40, 44, 52));
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        // --- TOP PANEL (Title) ---
        JLabel lblTitle = new JLabel("SETTINGS", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 48));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(60, 0, 40, 0)); 
        contentPane.add(lblTitle, BorderLayout.NORTH);

        // --- CENTER PANEL (Checkboxes) ---
        JPanel optionsPanel = new JPanel();
        optionsPanel.setBackground(new Color(40, 44, 52));
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        
        
        //load current settings
        this.loadSettings();
        
        //initialize options
        this.chkAutoReverse = createCheckBox("Auto-Reverse Board", this.settings.isAutoReverseBoard());
        this.chkAutoPromote = createCheckBox("Auto-Promote to Queen", this.settings.isAutoPromoteQueen());
        this.chkShowCoords = createCheckBox("Show Board Coordinates", this.settings.isShowCoordinates());

        //check changes every click on any checkbox
        ActionListener changeListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkChanges();
            }
        };
        
        this.chkAutoReverse.addActionListener(changeListener);
        this.chkAutoPromote.addActionListener(changeListener);
        this.chkShowCoords.addActionListener(changeListener);

        optionsPanel.add(Box.createVerticalGlue());
        optionsPanel.add(this.chkAutoReverse);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        optionsPanel.add(this.chkAutoPromote);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        optionsPanel.add(this.chkShowCoords);
        optionsPanel.add(Box.createVerticalGlue());

        contentPane.add(optionsPanel, BorderLayout.CENTER);

        // --- BOTTOM PANEL (Buttons) ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(40, 44, 52));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));

        this.btnApply = createBtnSettings("Apply Changes");
        this.btnApply.setVisible(false); //starts invisible bc there are no changes

        JButton btnBack = createBtnSettings("Go Back to Menu");

        buttonPanel.add(btnApply);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(btnBack);

        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        // --- BUTTON ACTIONS ---
        this.btnApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyChanges();
            }
        });
        
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBack();
            }
        });
    }

    // --- HELPER METHODS ---
    
    /**
     * Helper factory method to create and format a standardized JCheckBox component.
     * * @param text     The description label next to the checkbox.
     * @param selected The initial selected status of the checkbox.
     * @return A styled {@link JCheckBox} aligned for the options container.
     */
    private JCheckBox createCheckBox(String text, boolean selected) {
        JCheckBox chk = new JCheckBox(text, selected);
        chk.setFont(new Font("Arial", Font.PLAIN, 22));
        chk.setForeground(Color.WHITE);
        chk.setBackground(new Color(40, 44, 52));
        chk.setFocusPainted(false);
        chk.setAlignmentX(Component.CENTER_ALIGNMENT);
        return chk;
    }
    
    /**
     * Helper factory method to create and format a standardized JButton component for settings.
     * * @param texto The text label to display on the button.
     * @return A styled {@link JButton} optimized for the settings layout.
     */
    private JButton createBtnSettings(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT); 
        btn.setMaximumSize(new Dimension(250, 50)); 
        btn.setFocusPainted(false);
        return btn;
    }
    
    /**
     * Checks if the state of any interactive checkbox differs from the currently saved configurations.
     * * @return True if there are modified settings waiting to be saved, false otherwise.
     */
    private boolean hasUnsavedChanges() {
        return (this.chkAutoReverse.isSelected() != this.settings.isAutoReverseBoard()) ||
                (this.chkAutoPromote.isSelected() != this.settings.isAutoPromoteQueen()) ||
                (this.chkShowCoords.isSelected() != this.settings.isShowCoordinates());
    }

    /**
     * Refreshes the visibility state of the "Apply Changes" button based on 
     * whether unsaved changes are detected.
     */
    private void checkChanges() {
        this.btnApply.setVisible(this.hasUnsavedChanges());
    }

    /**
     * Flushes the current checkbox states into the memory model and attempts to write 
     * the updated configuration to the local disk using {@link SettingsFileManager}.
     * Hides the apply button if successful, or displays an error dialog if writing fails.
     */
    private void applyChanges() {
        
        this.settings.setAutoReverseBoard(this.chkAutoReverse.isSelected());
        this.settings.setAutoPromoteQueen(this.chkAutoPromote.isSelected());
        this.settings.setShowCoordinates(this.chkShowCoords.isSelected());
        try {
            SettingsFileManager.saveSettings(this.settings);
            this.btnApply.setVisible(false); 
        }catch(SavingException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error saving settings: " + e.getMessage(),
                    "Couldn't apply new settings",
                    JOptionPane.ERROR_MESSAGE
                );
        }
        
        
    }

    /**
     * Closes this settings window and brings the player back to the {@link MenuWindow}.
     * If unsaved changes are present, triggers a warning confirmation dialog before discarding inputs.
     */
    private void goBack() {
        boolean goBack = true;
        if(this.hasUnsavedChanges()) {
            String[] options = {"Yes", "No"};
            int response = JOptionPane.showOptionDialog(
                    null,
                    "Are you sure you want to exit without saving your changes?",
                    "Unsaved Changes",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[1]
                );
            
            if (response == JOptionPane.NO_OPTION) {
                goBack = false;
            } 
            
        }
        if(goBack) {
            MenuWindow menu = new MenuWindow();
            menu.setVisible(true);
            this.dispose();
        }
        
    }
    
    /**
     * Safely triggers data fetching from disk via {@link SettingsFileManager} 
     * to populate configurations. Instantiates standard configurations as a backup mechanism
     * if an external failure occurs.
     */
    private void loadSettings() {
        try {
            this.settings = SettingsFileManager.loadSettings();
        }catch(LoadingException e) {
            JOptionPane.showMessageDialog(
                null,
                "Error loading settings: " + e.getMessage(),
                "Settings will be reset to default",
                JOptionPane.ERROR_MESSAGE
            );
            this.settings = new Settings();
        }
    }
}