package com.projects.gui;

import com.projects.models.Prefab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;

/**
 * Created by Dan on 6/9/2015.
 */
public class PrefabCreationControl
{
    public static final int OK = 0;
    private static final int NOT_OK = 1;

    private JLabel nameLabel;
    private JLabel suffixLabel;
    private JLabel infoLabel;
    private JTextField nameField;
    private JTextField suffixField;
    private JPanel creationPanel;
    private JDialog creationDialog;
    private Collection<Prefab> prefabs;
    private int result;

    public PrefabCreationControl(JFrame frame, Collection<Prefab> prefabList)
    {
        prefabs = prefabList;
        nameField = new JTextField(14);
        suffixField = new JTextField(5);
        infoLabel = new JLabel("Enter a unique name and suffix.");
        nameLabel = new JLabel("Prefab's Name: ");
        suffixLabel = new JLabel("Member Suffix: ");
        creationPanel = new JPanel(new BorderLayout(10,10));
        JPanel infoPanel = new JPanel(new FlowLayout());
        infoPanel.add(infoLabel);
        creationPanel.add(infoPanel, BorderLayout.PAGE_START);

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(Box.createHorizontalStrut(15));
        inputPanel.add(suffixLabel);
        inputPanel.add(suffixField);
        creationPanel.add(inputPanel, BorderLayout.CENTER);

        AbstractAction okAction = new AbstractAction("OK") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!conflictsWithOtherPrefabs())
                {
                    creationDialog.setVisible(false);
                    result = OK;
                }
            }
        };

        AbstractAction cancelAction = new AbstractAction("Cancel") {
            @Override
            public void actionPerformed(ActionEvent e) {
                result = NOT_OK;
                creationDialog.setVisible(false);
            }
        };

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(new JButton(okAction));
        buttonPanel.add(new JButton(cancelAction));
        creationPanel.add(buttonPanel, BorderLayout.PAGE_END);

        creationDialog = new JDialog(frame, "Prefab Creation", true);
        creationDialog.setContentPane(creationPanel);
        creationDialog.pack();
        creationDialog.setSize(new Dimension(400, 160));
        creationDialog.setResizable(false);
        creationDialog.setVisible(true);
    }

    Boolean conflictsWithOtherPrefabs()
    {
        String newName = nameField.getText();
        String newSuffix = suffixField.getText();

        if (newName.equals(""))
        {
            infoLabel.setText("Name cannot be blank");
            return true;
        }

        if (newSuffix.equals(""))
        {
            infoLabel.setText("Suffix cannot be blank");
            return true;
        }

        for (Prefab prefab : prefabs)
        {
            if (prefab.getName().equals(newName))
            {
                infoLabel.setText("A prefab with this name already exists");
                return true;
            }

            if (prefab.getMemberSuffix().equals(newSuffix))
            {
                infoLabel.setText("A prefab with this suffix already exists");
                return true;
            }
        }

        return false;
    }

    public int getResult()
    {
        return result;
    }

    public String getName()
    {
        return nameField.getText();
    }

    public String getSuffix()
    {
        return suffixField.getText();
    }
}
