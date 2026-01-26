package it.smartlibrary.ui;

import it.smartlibrary.service.BackupService;

import javax.swing.*;
import java.awt.*;
import java.io.File;

///Dialog che permette di eseguire backup e restore del database
public class BackupDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private final BackupService backupService = new BackupService();
    private final DefaultListModel<File> model = new DefaultListModel<>();
    private final JList<File> listaBackups = new JList<>(model);

    public BackupDialog(Frame owner) {
        super(owner, "Backup e Restore", true);

        setLayout(new BorderLayout(10, 10));

        //Lista backup disponibili
        JPanel center = new JPanel(new BorderLayout());
        center.add(new JLabel("Backup disponibili:"), BorderLayout.NORTH);
        center.add(new JScrollPane(listaBackups), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        //Pulsanti
        JButton btnBackup = new JButton("Crea Backup");
        JButton btnRestore = new JButton("Ripristina Selezionato");

        JPanel bottom = new JPanel();
        bottom.add(btnBackup);
        bottom.add(btnRestore);

        add(bottom, BorderLayout.SOUTH);

        //Azione crea backup
        btnBackup.addActionListener(e -> creaBackup());

        //Azione di restore
        btnRestore.addActionListener(e -> restoreBackup());

        caricaListaBackups();

        setSize(400, 300);
        setLocationRelativeTo(owner);
    }

    private void caricaListaBackups() {
        model.clear();
        File[] files = backupService.listBackups();
        if (files != null) {
            for (File f : files) model.addElement(f);
        }
    }

    private void creaBackup() {
        new SwingWorker<File, Void>() {
            @Override
            protected File doInBackground() throws Exception {
                return backupService.backup();
            }

            @Override
            protected void done() {
                try {
                    File f = get();
                    JOptionPane.showMessageDialog(BackupDialog.this,
                            "Backup creato: " + f.getName());
                    caricaListaBackups();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(BackupDialog.this,
                            "Errore backup: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void restoreBackup() {
        File selected = listaBackups.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Seleziona un backup.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Ripristinare il backup selezionato?",
                "Conferma", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                backupService.restore(selected);
                return null;
            }

            @Override
            protected void done() {
                JOptionPane.showMessageDialog(BackupDialog.this,
                        "Ripristino completato. Riavvia l'applicazione.");
            }
        }.execute();
    }
}
