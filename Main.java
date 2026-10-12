import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class Main extends JFrame {
    JTextField tfId = new JTextField();
    JTextField tfNama = new JTextField();
    JTextField tfNim = new JTextField();
    String[] jkOpt = {"Laki-laki", "Perempuan"};
    JComboBox<String> cbJk = new JComboBox<>(jkOpt);
    
    JButton btnSave = new JButton("Add");
    JButton btnDel = new JButton("Delete");
    JButton btnClear = new JButton("Cancel");
    
    JTable table = new JTable();
    DefaultTableModel model = new DefaultTableModel();
    
    int rowIdx = -1;

    public Main() {
        setTitle("TP4 DPBO");
        setSize(550, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel l1 = new JLabel("ID"); l1.setBounds(20, 20, 80, 25); add(l1);
        tfId.setBounds(110, 20, 150, 25); add(tfId);

        JLabel l2 = new JLabel("Nama"); l2.setBounds(20, 50, 80, 25); add(l2);
        tfNama.setBounds(110, 50, 150, 25); add(tfNama);

        JLabel l3 = new JLabel("NIM"); l3.setBounds(20, 80, 80, 25); add(l3);
        tfNim.setBounds(110, 80, 150, 25); add(tfNim);

        JLabel l4 = new JLabel("JK"); l4.setBounds(20, 110, 80, 25); add(l4);
        cbJk.setBounds(110, 110, 150, 25); add(cbJk);

        btnSave.setBounds(110, 150, 80, 30); add(btnSave);
        btnDel.setBounds(200, 150, 80, 30); btnDel.setVisible(false); add(btnDel);
        btnClear.setBounds(290, 150, 80, 30); add(btnClear);

        model.addColumn("ID");
        model.addColumn("Nama");
        model.addColumn("NIM");
        model.addColumn("Jenis Kelamin");
        table.setModel(model);

        JScrollPane pane = new JScrollPane(table);
        pane.setBounds(20, 200, 490, 140);
        add(pane);

        loadData();

        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                rowIdx = table.getSelectedRow();
                tfId.setText(model.getValueAt(rowIdx, 0).toString());
                tfId.setEditable(false);
                tfNama.setText(model.getValueAt(rowIdx, 1).toString());
                tfNim.setText(model.getValueAt(rowIdx, 2).toString());
                cbJk.setSelectedItem(model.getValueAt(rowIdx, 3).toString());
                
                btnSave.setText("Update");
                btnDel.setVisible(true);
            }
        });

        btnSave.addActionListener(e -> {
            if (btnSave.getText().equals("Add")) {
                insertData();
            } else {
                updateData();
            }
        });

        btnDel.addActionListener(e -> deleteData());
        btnClear.addActionListener(e -> reset());
    }

    void loadData() {
        model.setRowCount(0);
        try {
            Connection c = Database.connect();
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery("SELECT * FROM orang");
            while (r.next()) {
                model.addRow(new Object[]{
                    r.getString("id"),
                    r.getString("nama"),
                    r.getString("nim"),
                    r.getString("jenis_kelamin")
                });
            }
            c.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    void insertData() {
        String id = tfId.getText();
        String nama = tfNama.getText();
        String nim = tfNim.getText();
        String jk = cbJk.getSelectedItem().toString();

        if (id.isEmpty() || nama.isEmpty() || nim.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Isi semua pak cik!");
            return;
        }

        try {
            Connection c = Database.connect();
            PreparedStatement check = c.prepareStatement("SELECT * FROM orang WHERE id=?");
            check.setString(1, id);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "ID udah ada!");
                c.close();
                return;
            }

            PreparedStatement ps = c.prepareStatement("INSERT INTO orang VALUES (?, ?, ?, ?)");
            ps.setString(1, id);
            ps.setString(2, nama);
            ps.setString(3, nim);
            ps.setString(4, jk);
            ps.executeUpdate();
            c.close();

            JOptionPane.showMessageDialog(this, "Berhasil tambah data");
            reset();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    void updateData() {
        String id = tfId.getText();
        String nama = tfNama.getText();
        String nim = tfNim.getText();
        String jk = cbJk.getSelectedItem().toString();

        if (nama.isEmpty() || nim.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kolom g boleh kosng!");
            return;
        }

        try {
            Connection c = Database.connect();
            PreparedStatement ps = c.prepareStatement("UPDATE orang SET nama=?, nim=?, jenis_kelamin=? WHERE id=?");
            ps.setString(1, nama);
            ps.setString(2, nim);
            ps.setString(3, jk);
            ps.setString(4, id);
            ps.executeUpdate();
            c.close();

            JOptionPane.showMessageDialog(this, "Berhasil update");
            reset();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    void deleteData() {
        int tanya = JOptionPane.showConfirmDialog(this, "Yakin di hapus banh?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (tanya == JOptionPane.YES_OPTION) {
            try {
                Connection c = Database.connect();
                PreparedStatement ps = c.prepareStatement("DELETE FROM orang WHERE id=?");
                ps.setString(1, tfId.getText());
                ps.executeUpdate();
                c.close();

                JOptionPane.showMessageDialog(this, "Data terhapus");
                reset();
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    void reset() {
        tfId.setText("");
        tfId.setEditable(true);
        tfNama.setText("");
        tfNim.setText("");
        cbJk.setSelectedIndex(0);
        btnSave.setText("Add");
        btnDel.setVisible(false);
        rowIdx = -1;
        table.clearSelection();
    }

    public static void main(String[] args) {
        new Main().setVisible(true);
    }
}