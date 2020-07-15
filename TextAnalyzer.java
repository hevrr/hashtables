import javafx.util.Pair;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.HashMap;
import java.util.StringTokenizer;

public class TextAnalyzer extends JPanel implements Runnable {

    /* holds text and analysis information */
    private static LinkedList<Concordance> concordances = new LinkedList<>();

    /* holds instructions for rewriting later */
    private static Queue<String> instructions = new Queue<>();

    /* file location for reading/writing and size */
    private static final String MASTER_TEXT_LOCATION = "src/TextLocations.txt";

    /* graphics */
    private static JFrame f;

    /* application dimensions */
    private static final int APPLICATION_WIDTH = 800;
    private static final int APPLICATION_HEIGHT = 520;

    /* currently selected text index */
    private static int textIndex;

    /* main */
    public static void main(String[] args) {
        init();
    }

    /* initialize */
    private static void init() {
        /* instructions */
        System.out.println("- Can upload texts");
        System.out.println("- Saves uploaded texts");
        System.out.println("- Cool thing I did: instead of storing the relevant String to display in each occurrence, I stored the indices of the text to save space");

        /* loads relevant texts */
        loadTextsFrom(new File(MASTER_TEXT_LOCATION));

        /* swing application */
        SwingUtilities.invokeLater(TextAnalyzer::start);
    }

    /* graphics settings */
    private TextAnalyzer() {
        setPreferredSize(new Dimension(APPLICATION_WIDTH, APPLICATION_HEIGHT));
        setFocusable(true);
        initializeJObjects();
    }

    /* JFrame settings */
    private static void start() {
        f = new JFrame();
        f.setTitle("TextAnalyzer.java");
        f.setResizable(false);
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                /* writes the newly uploaded texts and updates text location */
                writeTexts();
                System.exit(0);
            }
        });
        f.add(new TextAnalyzer(), BorderLayout.CENTER);
        f.pack();
        f.setVisible(true);
    }

    /* all JObjects */
    private JTextArea displayText;
    private JScrollPane occurrencePane;
    private JList<String> occurrences;
    private JScrollPane mostUsedPane;
    private JList<Object> mostUsed;
    private JList<String> stats;
    private DefaultListModel<String> statModel;
    private JTextField input;
    private JButton uploadText;
    private JComboBox<String> selectText;

    /* initializes and creates all JObjects */
    private void initializeJObjects() {
        /* initial text is the first one */
        textIndex = 0;

        /* create the text display */
        createDisplayText();

        /* creates the occurrences pane */
        occurrencePane = new JScrollPane();
        createScrollPane(null);
        f.getContentPane().add(occurrencePane);

        /* creates the stats */
        createStats();
        f.getContentPane().add(stats);

        /* creates textField */
        createInput();
        f.getContentPane().add(input);

        /* creates most used words occurrences */
        mostUsedPane = new JScrollPane();
        createMostUsedPane();
        f.getContentPane().add(mostUsedPane);

        /* creates upload text button */
        createUploadText();
        f.getContentPane().add(uploadText);

        /* creates select text combo box */
        createSelectText();
        f.getContentPane().add(selectText);
    }

    private void createSelectText() {
        selectText = new JComboBox<>();
        selectText.setBounds(45, 440, 160, 30);
        for (Concordance c : concordances)
            selectText.addItem(c.getText().title);
        selectText.addActionListener(e -> {
            textIndex = selectText.getSelectedIndex();

            createMostUsedPane();
            createScrollPane(null);

            displayText.setText("");
        });
    }

    private void createInput() {
        input = new JTextField();
        input.setBounds(45, 46, 160, 38);
        input.setColumns(10);
        input.addActionListener(e -> {
            createScrollPane(null);

            statModel.clear();
            occurrences.clearSelection();

            if (concordances.get(textIndex).containsSearch(input.getText())) {
                LinkedList<Occurrences> data = concordances.get(textIndex).getOccurrences(input.getText());
                createScrollPane(data);
                statModel.addElement("Entries: " + data.size());
            } else
                statModel.addElement("Entries: 0");
            statModel.addElement("Number of texts: " + concordances.size());
        });
    }

    private void createScrollPane(LinkedList<Occurrences> d) {
        DefaultListModel<String> occurrenceModel = new DefaultListModel<>();
        occurrences = new JList<>(occurrenceModel);
        occurrencePane.setBounds(225, 50, 145, 420);
        occurrencePane.setViewportView(occurrences);
        occurrences.setLayoutOrientation(JList.VERTICAL);
        occurrences.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                LinkedList<Occurrences> data = concordances.get(textIndex).getOccurrences(input.getText());
                displayText.setText(concordances.get(textIndex).getText().text.substring(concordances.get(textIndex).getText().indices.get(data.get(occurrences.getSelectedIndex()).bodyNumber - 1).getKey(), concordances.get(textIndex).getText().indices.get(data.get(occurrences.getSelectedIndex()).bodyNumber - 1).getValue()));
            }
        });
        if (d != null)
            for (Occurrences occurrences : d)
                occurrenceModel.addElement(occurrences.information);
    }

    private void createMostUsedPane() {
        DefaultListModel<Object> m = new DefaultListModel<>();
        mostUsed = new JList<>(m);
        mostUsedPane.setBounds(50, 100, 150, 200);
        mostUsedPane.setViewportView(mostUsed);
        mostUsed.setLayoutOrientation(JList.VERTICAL);

        mostUsed.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                input.setText(mostUsed.getSelectedValue().toString());
                input.postActionEvent();
            }
        });

        for (int i = concordances.get(textIndex).getText().commonWords.length - 1; i >= 0; i--)
            for (int j = 0; j < concordances.get(textIndex).getText().commonWords[i].size(); j++)
                m.addElement(concordances.get(textIndex).getText().commonWords[i].get(j));
    }

    private void createDisplayText() {
        displayText = new JTextArea();
        displayText.setLineWrap(true);
        displayText.setWrapStyleWord(true);
        displayText.setBounds(390, 46, 365, 428);
        f.getContentPane().add(displayText);
    }

    private void createStats() {
        statModel = new DefaultListModel<>();
        stats = new JList<>(statModel);
        stats.setSelectionInterval(-1, -1);
        stats.setBounds(50, 320, 150, 50);
        stats.addListSelectionListener(e -> stats.clearSelection());
        statModel.addElement("Number of texts: " + concordances.size());
    }

    private void createUploadText() {
        uploadText = new JButton("Upload text");
        uploadText.setBounds(47, 390, 156, 33);
        uploadText.addActionListener(e -> {
            /* chooser parameters */
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt extensions", "txt");
            fileChooser.setFileFilter(filter);

            /* if file selected */
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                /* check if sonnet */
                int dialogue = JOptionPane.YES_NO_OPTION;
                int in = JOptionPane.showConfirmDialog(null, "Is this a sonnet", "?", dialogue);
                if (in == JOptionPane.YES_OPTION)
                    loadText(fileChooser.getSelectedFile(), true, false);
                else
                    loadText(fileChooser.getSelectedFile(), false, false);
                selectText.addItem(concordances.get(concordances.size() - 1).getText().title);
                selectText.setSelectedIndex(concordances.size() - 1);
            }
        });
    }

    /* loads all texts from a master document */
    private static void loadTextsFrom(File file) {
        try {
            /* buffered reader for reading */
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                if (line.charAt(0) != '/')
                    loadText(new File(line.substring(1)), line.charAt(0) == 'S', true);
                else
                    instructions.enqueue(line);
                line = reader.readLine();
            }
            textIndex = 0;
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* loads texts */
    private static void loadText(File file, boolean isSonnet, boolean exists) {
        try {
            /* buffered reader for reading */
            BufferedReader reader = new BufferedReader(new FileReader(file));
            /* hashTable */
            HashTable<String, LinkedList<Occurrences>> hashTable = new HashTable<>();
            LinkedList[] commonWords;

            /* for tracking word occurrences */
            HashMap<String, Integer> hashMap = new HashMap<>();

            /* variables for storing text information for later use */
            int maxOccurrence = 1;
            int index = 0;
            int paragraphNumber = 1;
            int tracker = 1;
            int chapterNumber = 0;
            int previousParagraphs = 1;

            /* string trackers */
            StringBuilder text = new StringBuilder();
            Text t = new Text(file.getName(), text.toString(), file.getPath(), isSonnet, exists);
            String line = reader.readLine();

            /* loop until all text is loaded */
            while (line != null) {

                /* every new paragraph, reset word count */
                if (!isSonnet)
                    tracker = 1;

                /* every new sonnet, increase sonnet number, reset line number, update start index and end index for text */
                if (isSonnet && line.equals("")) {
                    paragraphNumber++;
                    t.indices.add(new Pair<>(index, text.length() - 1));
                    index = text.length() + 1;
                    tracker = 0;
                } else if (!isSonnet && line.length() > 0 && line.charAt(0) == '/') {
                    paragraphNumber = 1;
                    chapterNumber++;
                    tracker = 1;
                    index += line.length();
                }

                /* tokenizer */
                StringTokenizer tokenizer = new StringTokenizer(line, " —/“,;:.\"”!?");

                /* loop until all tokens have been loaded */
                while (tokenizer.hasMoreTokens()) {

                    /* individual word */
                    String word = tokenizer.nextToken().toLowerCase();
                    LinkedList<Occurrences> data;

                    /* if already contained, update count */
                    if (hashTable.contains(word)) {
                        data = hashTable.get(word);

                        /* if not, create new data */
                    } else
                        data = new LinkedList<>();

                    /* checks if item already exists for sonnets */
                    boolean alreadyExists = false;
                    for (Occurrences datum : data)
                        if (datum.information.equals("S" + paragraphNumber + " L" + tracker) || datum.information.equals("C" + chapterNumber + " P" + paragraphNumber)) {
                            alreadyExists = true;
                            break;
                        }

                    /* adds new pointer and information to val in hashtable */
                    if (!alreadyExists)
                        if (!isSonnet)
                            data.add(new Occurrences(previousParagraphs, "Ch." + chapterNumber + " Par." + paragraphNumber));
                        else
                            data.add(new Occurrences(paragraphNumber, "Son." + paragraphNumber + " Line." + tracker));
                    hashTable.put(word, data);

                    /* if hashmap contains word, then increase occurrence count */
                    if (hashMap.containsKey(word)) {
                        hashMap.put(word, hashMap.get(word) + 1);

                        /* keep updating max occurrences if greater */
                        if (hashMap.get(word) > maxOccurrence)
                            maxOccurrence = hashMap.get(word);

                        /* if not, then put word in */
                    } else
                        hashMap.put(word, 1);

                    /* increase word tracker for text */
                    if (!isSonnet)
                        tracker++;
                }

                /* increase line number for sonnets */
                if (isSonnet)
                    tracker++;

                /* every new paragraph, store index pointer, update index and paragraph number */
                if (!isSonnet && !line.equals("") && !line.contains("/")) {
                    t.indices.add(new Pair<>(index, index + line.length()));
                    index += line.length();
                    paragraphNumber++;
                    previousParagraphs++;
                }

                text.append(line);
                if (isSonnet)
                    text.append("\n");
                /* format and read next line */
                line = reader.readLine();
            }
            /* creates an array where the index corresponds to number of times a word has occurred */
            commonWords = new LinkedList[maxOccurrence + 1];
            for (int j = 0; j < maxOccurrence + 1; j++)
                commonWords[j] = new LinkedList<>();

            /* add words to linkedlist inside the array in corresponding index */
            hashMap.forEach((k, v) -> commonWords[v].add(k));

            /* add hashtable into linkedlist */
            t.commonWords = commonWords;
            t.text = text.toString();
            Concordance c = new Concordance(t);
            c.setTable(hashTable);
            concordances.add(c);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeTexts() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(MASTER_TEXT_LOCATION));
            while (!instructions.isEmpty()) {
                writer.write(instructions.dequeue());
                writer.newLine();
            }
            for (Concordance c : concordances) {
                Text text = c.getText();
                String t = text.location;
                if (text.fileStored) {
                    if (text.isSonnet)
                        t = "S" + t;
                    else
                        t = "T" + t;
                } else {
                    File file = new File("src/" + text.title);
                    file.createNewFile();
                    PrintWriter out = new PrintWriter(file);
                    out.println(text.text);
                    if (text.isSonnet)
                        t = "S" + file.getPath();
                    else
                        t = "T" + file.getPath();
                    BufferedReader reader = new BufferedReader(new FileReader(text.location));
                    BufferedWriter writer2 = new BufferedWriter(new FileWriter(file.getPath()));
                    String line;
                    while((line = reader.readLine()) != null) {
                        writer2.write(line);
                        writer2.newLine();
                    }
                    writer2.close();
                    reader.close();
                }
                writer.write(t);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
    }
}
