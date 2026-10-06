
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class LostLinkApp extends JFrame {
    static final Color NAVY = new Color(15, 31, 55);
    static final Color BLUE = new Color(47, 99, 218);
    static final Color BLUE2 = new Color(81, 126, 235);
    static final Color BG = new Color(246, 248, 252);
    static final Color CARD = Color.WHITE;
    static final Color TEXT = new Color(35, 45, 62);
    static final Color MUTED = new Color(113, 124, 143);
    static final Color BORDER = new Color(225, 230, 238);
    static final Color GREEN = new Color(39, 151, 105);
    static final Color ORANGE = new Color(229, 142, 44);
    static final String DATA = "lostlink_data.dat";

    ArrayList<Item> items = new ArrayList<>();
    String user = "";
    boolean admin = false;
    JPanel content;
    JLabel pageTitle;
    CardLayout cardLayout;
    JPanel mainCards;

    static class Item implements Serializable {
        String id, type, name, category, location, date, description, reporter, status, claimBy;
        Item(String type,String name,String category,String location,String date,String desc,String reporter){
            this.id="LL-"+(1000+new Random().nextInt(9000));
            this.type=type; this.name=name; this.category=category; this.location=location;
            this.date=date; this.description=desc; this.reporter=reporter;
            this.status="OPEN"; this.claimBy="";
        }
    }

    public LostLinkApp(){
        load();
        setTitle("LostLink");
        setSize(1180,760);
        setMinimumSize(new Dimension(1050,680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        showLogin();
    }

    void load(){
        try(ObjectInputStream in=new ObjectInputStream(new FileInputStream(DATA))){
            items=(ArrayList<Item>)in.readObject();
        }catch(Exception e){
            items=new ArrayList<>();
            items.add(new Item("FOUND","Black Wallet","Personal","CSE Block","05 Oct 2026","Black leather wallet with student card","Demo User"));
            items.add(new Item("LOST","AirPods Case","Electronics","Library","04 Oct 2026","White charging case","Demo User"));
            save();
        }
    }
    void save(){
        try(ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(DATA))){out.writeObject(items);}
        catch(Exception ignored){}
    }

    Font f(int size, int style){ return new Font("Segoe UI",style,size); }

    JPanel rounded(Color bg, int radius){
        JPanel p=new JPanel(){
            protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg); g2.fillRoundRect(0,0,getWidth()-1,getHeight()-1,radius,radius);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        return p;
    }

    JButton navButton(String text){
        JButton b=new JButton(text);
        b.setFont(f(14,Font.BOLD)); b.setForeground(new Color(225,232,244));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(new EmptyBorder(12,18,12,12));
        b.setContentAreaFilled(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    JButton primary(String text){
        JButton b=new JButton(text);
        b.setFont(f(14,Font.BOLD)); b.setForeground(Color.WHITE);
        b.setBackground(BLUE); b.setBorder(new EmptyBorder(11,20,11,20));
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    JLabel title(String s,int size){ JLabel l=new JLabel(s); l.setFont(f(size,Font.BOLD)); l.setForeground(TEXT); return l; }
    JLabel muted(String s){ JLabel l=new JLabel(s); l.setFont(f(13,Font.PLAIN)); l.setForeground(MUTED); return l; }

    void showLogin(){
        JPanel bg=new JPanel(new GridBagLayout()); bg.setBackground(BG);
        JPanel card=rounded(CARD,24); card.setPreferredSize(new Dimension(430,500));
        card.setBorder(new EmptyBorder(35,42,35,42)); card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));

        JLabel logo=new JLabel("LOSTLINK"); logo.setFont(f(34,Font.BOLD)); logo.setForeground(NAVY);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sub=new JLabel("Lost & Found Management"); sub.setFont(f(15,Font.PLAIN));
        sub.setForeground(MUTED); sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(logo); card.add(Box.createVerticalStrut(5)); card.add(sub);
        card.add(Box.createVerticalStrut(35));
        card.add(fieldLabel("Username"));
        JTextField u=input(); card.add(u); card.add(Box.createVerticalStrut(16));
        card.add(fieldLabel("Password"));
        JPasswordField p=new JPasswordField(); styleInput(p); card.add(p);
        card.add(Box.createVerticalStrut(25));
        JButton login=primary("Sign in"); login.setAlignmentX(Component.CENTER_ALIGNMENT); card.add(login);
        card.add(Box.createVerticalStrut(18));
        JLabel hint=muted("User: user / user123    Admin: admin / admin123");
        hint.setAlignmentX(Component.CENTER_ALIGNMENT); card.add(hint);

        login.addActionListener(e->{
            String us=u.getText().trim(), pw=new String(p.getPassword());
            if(us.equals("user")&&pw.equals("user123") || us.equals("admin")&&pw.equals("admin123")){
                user=us; admin=us.equals("admin"); showApp();
            } else JOptionPane.showMessageDialog(this,"Invalid username or password.","LostLink",JOptionPane.ERROR_MESSAGE);
        });
        bg.add(card);
        setContentPane(bg); revalidate(); repaint();
    }

    JLabel fieldLabel(String s){ JLabel l=new JLabel(s); l.setFont(f(13,Font.BOLD)); l.setForeground(TEXT); l.setAlignmentX(Component.LEFT_ALIGNMENT); return l; }
    JTextField input(){ JTextField t=new JTextField(); styleInput(t); return t; }
    void styleInput(JTextField t){
        t.setFont(f(14,Font.PLAIN)); t.setMaximumSize(new Dimension(Integer.MAX_VALUE,44));
        t.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),new EmptyBorder(9,12,9,12)));
        t.setBackground(new Color(251,252,254));
    }

    void showApp(){
        JPanel root=new JPanel(new BorderLayout()); root.setBackground(BG);
        JPanel side=new JPanel(); side.setBackground(NAVY); side.setPreferredSize(new Dimension(230,760));
        side.setBorder(new EmptyBorder(28,18,25,18)); side.setLayout(new BoxLayout(side,BoxLayout.Y_AXIS));

        JLabel brand=new JLabel("LOSTLINK"); brand.setFont(f(28,Font.BOLD)); brand.setForeground(Color.WHITE);
        JLabel brandSub=new JLabel("Lost & Found"); brandSub.setFont(f(12,Font.PLAIN)); brandSub.setForeground(new Color(166,181,205));
        side.add(brand); side.add(brandSub); side.add(Box.createVerticalStrut(35));

        JButton dash=navButton("Dashboard");
        JButton lost=navButton("Report Lost");
        JButton found=navButton("Report Found");
        JButton search=navButton("Search & Match");
        JButton claims=navButton("Claims");
        JButton records=navButton("Records");
        side.add(dash); side.add(lost); side.add(found); side.add(search); side.add(claims); side.add(records);
        side.add(Box.createVerticalGlue());
        if(admin){ JButton adm=navButton("Admin Panel"); side.add(adm); adm.addActionListener(e->showAdmin()); }
        JButton out=navButton("Sign out"); side.add(out);

        JPanel right=new JPanel(new BorderLayout()); right.setBackground(BG);
        JPanel header=new JPanel(new BorderLayout()); header.setOpaque(false);
        header.setBorder(new EmptyBorder(22,30,18,30));
        pageTitle=title("Dashboard",25);
        JPanel profile=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); profile.setOpaque(false);
        JLabel dot=new JLabel("●"); dot.setForeground(GREEN); dot.setFont(f(12,Font.PLAIN));
        JLabel who=new JLabel(admin?"Administrator":"Welcome, "+user); who.setFont(f(13,Font.BOLD)); who.setForeground(TEXT);
        profile.add(dot); profile.add(who);
        header.add(pageTitle,BorderLayout.WEST); header.add(profile,BorderLayout.EAST);

        content=new JPanel(new BorderLayout()); content.setOpaque(false); content.setBorder(new EmptyBorder(0,30,25,30));
        right.add(header,BorderLayout.NORTH); right.add(content,BorderLayout.CENTER);
        root.add(side,BorderLayout.WEST); root.add(right,BorderLayout.CENTER);

        dash.addActionListener(e->dashboard());
        lost.addActionListener(e->report("LOST"));
        found.addActionListener(e->report("FOUND"));
        search.addActionListener(e->search());
        claims.addActionListener(e->claims());
        records.addActionListener(e->records());
        out.addActionListener(e->{user="";admin=false;showLogin();});

        setContentPane(root); dashboard(); revalidate(); repaint();
    }

    void dashboard(){
        pageTitle.setText("Dashboard");
        JPanel p=new JPanel(new BorderLayout(0,18)); p.setOpaque(false);

        JPanel welcome=rounded(CARD,18); welcome.setBorder(new EmptyBorder(20,24,20,24)); welcome.setLayout(new BorderLayout());
        JPanel wtxt=new JPanel(); wtxt.setOpaque(false); wtxt.setLayout(new BoxLayout(wtxt,BoxLayout.Y_AXIS));
        wtxt.add(title("Find what’s missing. Return what’s found.",21));
        wtxt.add(Box.createVerticalStrut(5));
        wtxt.add(muted("A simple place to report, search and recover belongings."));
        welcome.add(wtxt,BorderLayout.CENTER);
        JButton rl=primary("＋ Report an item"); welcome.add(rl,BorderLayout.EAST); rl.addActionListener(e->report("LOST"));
        p.add(welcome,BorderLayout.NORTH);

        JPanel stats=new JPanel(new GridLayout(1,3,15,0)); stats.setOpaque(false);
        long l=items.stream().filter(i->i.type.equals("LOST")&&i.status.equals("OPEN")).count();
        long fnd=items.stream().filter(i->i.type.equals("FOUND")&&i.status.equals("OPEN")).count();
        long c=items.stream().filter(i->!i.claimBy.isEmpty()).count();
        stats.add(statCard("Open lost reports",String.valueOf(l),BLUE));
        stats.add(statCard("Open found reports",String.valueOf(fnd),GREEN));
        stats.add(statCard("Claim requests",String.valueOf(c),ORANGE));
        p.add(stats,BorderLayout.CENTER);

        JPanel bottom=new JPanel(new GridLayout(1,2,15,0)); bottom.setOpaque(false);
        bottom.add(recentCard()); bottom.add(howCard());
        p.add(bottom,BorderLayout.SOUTH);
        content.removeAll(); content.add(p); content.revalidate(); content.repaint();
    }

    JPanel statCard(String a,String b,Color c){
        JPanel p=rounded(CARD,18); p.setBorder(new EmptyBorder(20,22,18,22)); p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        JLabel x=muted(a); p.add(x); p.add(Box.createVerticalStrut(8));
        JLabel y=new JLabel(b); y.setFont(f(31,Font.BOLD)); y.setForeground(c); p.add(y);
        return p;
    }

    JPanel recentCard(){
        JPanel p=rounded(CARD,18); p.setBorder(new EmptyBorder(18,20,18,20)); p.setLayout(new BorderLayout(0,10));
        p.add(title("Recent reports",17),BorderLayout.NORTH);
        String[] cols={"Item","Type","Location","Status"};
        DefaultTableModel m=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        for(Item i:items) m.addRow(new Object[]{i.name,i.type,i.location,i.status});
        JTable t=new JTable(m); t.setRowHeight(28); t.setFont(f(12,Font.PLAIN)); t.getTableHeader().setFont(f(12,Font.BOLD));
        p.add(new JScrollPane(t),BorderLayout.CENTER); return p;
    }

    JPanel howCard(){
        JPanel p=rounded(CARD,18); p.setBorder(new EmptyBorder(18,22,18,22)); p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.add(title("How LostLink works",17)); p.add(Box.createVerticalStrut(15));
        String[] a={"1. Report a lost or found item","2. Search the shared records","3. Request a claim","4. Admin verifies and closes the case"};
        for(String s:a){ JLabel l=new JLabel(s); l.setFont(f(13,Font.PLAIN)); l.setForeground(TEXT); l.setBorder(new EmptyBorder(8,0,8,0)); p.add(l); }
        return p;
    }

    JPanel form(String heading,String type){
        JPanel p=rounded(CARD,18); p.setBorder(new EmptyBorder(22,25,22,25)); p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.add(title(heading,20)); p.add(Box.createVerticalStrut(18));
        JTextField name=input(), cat=input(), loc=input(), date=input(); date.setText(LocalDate.now().toString());
        JTextArea desc=new JTextArea(5,30); desc.setFont(f(14,Font.PLAIN)); desc.setLineWrap(true); desc.setWrapStyleWord(true);
        desc.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),new EmptyBorder(9,12,9,12)));
        p.add(row("Item name",name)); p.add(row("Category",cat)); p.add(row("Location",loc)); p.add(row("Date",date)); p.add(row("Description",new JScrollPane(desc)));
        p.add(Box.createVerticalStrut(10));
        JButton save=primary("Submit report"); p.add(save);
        save.addActionListener(e->{
            if(name.getText().trim().isEmpty()||loc.getText().trim().isEmpty()){JOptionPane.showMessageDialog(this,"Item name and location are required.");return;}
            Item it=new Item(type,name.getText().trim(),cat.getText().trim(),loc.getText().trim(),date.getText().trim(),desc.getText().trim(),user);
            items.add(it); save();
            JOptionPane.showMessageDialog(this,"Report submitted.\nReport ID: "+it.id);
            dashboard();
        });
        return p;
    }
    JPanel row(String lab,JComponent comp){
        JPanel r=new JPanel(new BorderLayout(15,5)); r.setOpaque(false); r.setMaximumSize(new Dimension(Integer.MAX_VALUE,70));
        JLabel l=fieldLabel(lab); l.setPreferredSize(new Dimension(105,40)); r.add(l,BorderLayout.WEST); r.add(comp,BorderLayout.CENTER); return r;
    }
    void report(String type){
        pageTitle.setText(type.equals("LOST")?"Report Lost Item":"Report Found Item");
        content.removeAll(); content.add(form(type.equals("LOST")?"Report a lost item":"Report a found item",type),BorderLayout.CENTER); content.revalidate();content.repaint();
    }

    void search(){
        pageTitle.setText("Search & Match");
        JPanel p=rounded(CARD,18); p.setBorder(new EmptyBorder(20,22,20,22)); p.setLayout(new BorderLayout(0,15));
        JPanel top=new JPanel(new BorderLayout(10,0)); top.setOpaque(false);
        JTextField q=input(); JButton go=primary("Search"); top.add(q,BorderLayout.CENTER); top.add(go,BorderLayout.EAST); p.add(top,BorderLayout.NORTH);
        String[] cols={"ID","Type","Item","Category","Location","Date","Status"};
        DefaultTableModel m=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        JTable t=new JTable(m); t.setRowHeight(30); t.setFont(f(12,Font.PLAIN)); t.getTableHeader().setFont(f(12,Font.BOLD)); p.add(new JScrollPane(t),BorderLayout.CENTER);
        JButton claim=primary("Request selected claim"); p.add(claim,BorderLayout.SOUTH);
        Runnable fill=()->{m.setRowCount(0);String k=q.getText().trim().toLowerCase();for(Item i:items){String all=(i.id+" "+i.name+" "+i.category+" "+i.location+" "+i.description).toLowerCase();if(k.isEmpty()||all.contains(k))m.addRow(new Object[]{i.id,i.type,i.name,i.category,i.location,i.date,i.status});}};
        go.addActionListener(e->fill.run()); fill.run();
        claim.addActionListener(e->{int r=t.getSelectedRow();if(r<0)return;String id=(String)m.getValueAt(r,0);for(Item i:items)if(i.id.equals(id)){i.claimBy=user;i.status="CLAIM_PENDING";save();JOptionPane.showMessageDialog(this,"Claim request submitted.");break;}});
        content.removeAll(); content.add(p); content.revalidate();content.repaint();
    }

    void claims(){
        pageTitle.setText(admin?"Claim Requests":"My Claims");
        JPanel p=rounded(CARD,18); p.setBorder(new EmptyBorder(20,22,20,22)); p.setLayout(new BorderLayout(0,15));
        String[] cols={"ID","Item","Reporter","Claimed by","Status"};
        DefaultTableModel m=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        JTable t=new JTable(m); t.setRowHeight(30); t.setFont(f(12,Font.PLAIN));t.getTableHeader().setFont(f(12,Font.BOLD));
        for(Item i:items) if(!i.claimBy.isEmpty()&&(admin||i.claimBy.equals(user)))m.addRow(new Object[]{i.id,i.name,i.reporter,i.claimBy,i.status});
        p.add(new JScrollPane(t),BorderLayout.CENTER);
        if(admin){JButton approve=primary("Approve selected");p.add(approve,BorderLayout.SOUTH);approve.addActionListener(e->{int r=t.getSelectedRow();if(r<0)return;String id=(String)m.getValueAt(r,0);for(Item i:items)if(i.id.equals(id)){i.status="CLAIMED";save();claims();break;}});}
        content.removeAll();content.add(p);content.revalidate();content.repaint();
    }

    void records(){
        pageTitle.setText("All Records");
        JPanel p=rounded(CARD,18);p.setBorder(new EmptyBorder(20,22,20,22));p.setLayout(new BorderLayout());
        String[] cols={"ID","Type","Item","Category","Location","Reporter","Status"};
        DefaultTableModel m=new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};
        JTable t=new JTable(m);t.setRowHeight(30);t.setFont(f(12,Font.PLAIN));t.getTableHeader().setFont(f(12,Font.BOLD));
        for(Item i:items)m.addRow(new Object[]{i.id,i.type,i.name,i.category,i.location,i.reporter,i.status});
        p.add(new JScrollPane(t));content.removeAll();content.add(p);content.revalidate();content.repaint();
    }

    void showAdmin(){
        pageTitle.setText("Admin Panel");
        JPanel p=rounded(CARD,18);p.setBorder(new EmptyBorder(25,25,25,25));p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        p.add(title("System overview",20));p.add(Box.createVerticalStrut(20));
        p.add(muted("Total reports: "+items.size()));p.add(Box.createVerticalStrut(10));
        p.add(muted("Lost reports: "+items.stream().filter(i->i.type.equals("LOST")).count()));
        p.add(Box.createVerticalStrut(10));p.add(muted("Found reports: "+items.stream().filter(i->i.type.equals("FOUND")).count()));
        p.add(Box.createVerticalStrut(10));p.add(muted("Pending claims: "+items.stream().filter(i->i.status.equals("CLAIM_PENDING")).count()));
        content.removeAll();content.add(p);content.revalidate();content.repaint();
    }

    public static void main(String[] args){SwingUtilities.invokeLater(()->new LostLinkApp().setVisible(true));}
}
