package ui;

import database.QuestionDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import models.Question;
import models.User;

public class AdminDashboardView extends JFrame 
{

    private final User adminUser;
    private final QuestionDAO questionDAO;

    public AdminDashboardView(User adminUser) 
    {
        this.adminUser = adminUser;
        this.questionDAO = new QuestionDAO();

        setTitle("QuizQuest - Admin Dashboard");
        setSize(420, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel titleLabel = new JLabel("QuizQuest Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        String username = (this.adminUser != null) ? this.adminUser.getUsername() : "Admin";
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        headerPanel.add(titleLabel);
        headerPanel.add(welcomeLabel);
        add(headerPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 12, 12));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        JButton btnAddQuestion = new JButton("Add Question");
        JButton btnViewQuestions = new JButton("View Questions");
        JButton btnDeleteQuestion = new JButton("Delete Question");
        JButton btnLogout = new JButton("Logout");

        buttonPanel.add(btnAddQuestion);
        buttonPanel.add(btnViewQuestions);
        buttonPanel.add(btnDeleteQuestion);
        buttonPanel.add(btnLogout);
        add(buttonPanel, BorderLayout.CENTER);

        btnAddQuestion.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                addQuestion();
            }
        });

        btnViewQuestions.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                viewQuestions();
            }
        });

        btnDeleteQuestion.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                deleteQuestion();
            }
        });

        btnLogout.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                dispose();
                new AuthView().setVisible(true);
            }
        });

        setVisible(true);
    }

    private void addQuestion() 
    {
        String topic = JOptionPane.showInputDialog(this, "Enter the topic (e.g., Science, Social Science):");
        if (topic == null || topic.trim().isEmpty()) return;

        String prompt = JOptionPane.showInputDialog(this, "Enter the question prompt:");
        if (prompt == null || prompt.trim().isEmpty()) return;

        String[] types = {"Single Choice MCQ", "True / False", "Multi-Select MCQ"};
        int selectedType = JOptionPane.showOptionDialog
        (
                this,
                "Select question type:",
                "Question Type",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                types,
                types[0]
        );

        if (selectedType == JOptionPane.CLOSED_OPTION) return;

        boolean success = false;

        if (selectedType == 0) 
        { 
            String opt1 = JOptionPane.showInputDialog(this, "Option 1:");
            if (opt1 == null || opt1.trim().isEmpty()) return;

            String opt2 = JOptionPane.showInputDialog(this, "Option 2:");
            if (opt2 == null || opt2.trim().isEmpty()) return;

            String opt3 = JOptionPane.showInputDialog(this, "Option 3:");
            if (opt3 == null || opt3.trim().isEmpty()) return;

            String opt4 = JOptionPane.showInputDialog(this, "Option 4:");
            if (opt4 == null || opt4.trim().isEmpty()) return;

            String[] choices = 
            {
                "1 (" + opt1.trim() + ")",
                "2 (" + opt2.trim() + ")",
                "3 (" + opt3.trim() + ")",
                "4 (" + opt4.trim() + ")"
            };

            int answerChoice = JOptionPane.showOptionDialog
            (
                    this,
                    "Select which option is the correct answer:",
                    "Correct Answer",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    choices,
                    choices[0]
            );

            if (answerChoice == JOptionPane.CLOSED_OPTION) return;

            String optionsCombined = opt1.trim() + "|" + opt2.trim() + "|" + opt3.trim() + "|" + opt4.trim();
            String answerIndex = String.valueOf(answerChoice + 1);

            success = questionDAO.addQuestion(topic.trim(), "MCQ", prompt.trim(), optionsCombined, answerIndex);

        } else if (selectedType == 1) 
        { 
            String[] tfOptions = {"True", "False"};
            int tfChoice = JOptionPane.showOptionDialog
            (
                    this,
                    "Select the correct answer:",
                    "Correct Answer",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    tfOptions,
                    tfOptions[0]
            );

            if (tfChoice == JOptionPane.CLOSED_OPTION) return;

            String answer = (tfChoice == 0) ? "true" : "false";

            success = questionDAO.addQuestion(topic.trim(), "TRUE_FALSE", prompt.trim(), "", answer);

        } else if (selectedType == 2) 
        {
            String opt1 = JOptionPane.showInputDialog(this, "Option 1:");
            if (opt1 == null || opt1.trim().isEmpty()) return;

            String opt2 = JOptionPane.showInputDialog(this, "Option 2:");
            if (opt2 == null || opt2.trim().isEmpty()) return;

            String opt3 = JOptionPane.showInputDialog(this, "Option 3:");
            if (opt3 == null || opt3.trim().isEmpty()) return;

            String opt4 = JOptionPane.showInputDialog(this, "Option 4:");
            if (opt4 == null || opt4.trim().isEmpty()) return;

            JPanel checkPanel = new JPanel(new GridLayout(5, 1, 4, 4));
            checkPanel.add(new JLabel("Select all options that are correct:"));
            JCheckBox cb1 = new JCheckBox("1. " + opt1.trim());
            JCheckBox cb2 = new JCheckBox("2. " + opt2.trim());
            JCheckBox cb3 = new JCheckBox("3. " + opt3.trim());
            JCheckBox cb4 = new JCheckBox("4. " + opt4.trim());
            checkPanel.add(cb1);
            checkPanel.add(cb2);
            checkPanel.add(cb3);
            checkPanel.add(cb4);

            int choice = JOptionPane.showConfirmDialog(
                this, 
                checkPanel, 
                "Select Correct Answers", 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.QUESTION_MESSAGE
            );

            if (choice != JOptionPane.OK_OPTION) return;

            List<String> selectedAnswers = new ArrayList<>();
            if (cb1.isSelected()) selectedAnswers.add("1");
            if (cb2.isSelected()) selectedAnswers.add("2");
            if (cb3.isSelected()) selectedAnswers.add("3");
            if (cb4.isSelected()) selectedAnswers.add("4");

            if (selectedAnswers.isEmpty()) 
            {
                JOptionPane.showMessageDialog(this, "You must select at least one correct option.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String optionsCombined = opt1.trim() + "|" + opt2.trim() + "|" + opt3.trim() + "|" + opt4.trim();
            String answerIndices = String.join(",", selectedAnswers);

            success = questionDAO.addQuestion(topic.trim(), "MULTI_SELECT", prompt.trim(), optionsCombined, answerIndices);
        }

        if (success) 
        {
            JOptionPane.showMessageDialog(this, "Question added and saved to database successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else 
        {
            JOptionPane.showMessageDialog(this, "Failed to save question to database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewQuestions() 
    {
        List<Question> questions = questionDAO.getAllQuestions();

        if (questions == null || questions.isEmpty()) 
        {
            JOptionPane.showMessageDialog(this, "No questions found in the database.", "All Questions", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder details = new StringBuilder();

        for (Question q : questions) 
        {
            details.append("[ID: ").append(q.getId()).append("] (")
                   .append(q.getPoints()).append(" pts)\n")
                   .append(q.getPrompt()).append("\n\n");
        }

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 350));

        JOptionPane.showMessageDialog(this, scrollPane, "All Questions (Total: " + questions.size() + ")", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteQuestion() 
    {
        String inputId = JOptionPane.showInputDialog(this, "Enter Question ID to delete:");
        if (inputId == null || inputId.trim().isEmpty()) return;

        try 
        {
            int questionId = Integer.parseInt(inputId.trim());
            boolean success = questionDAO.deleteQuestion(questionId);

            if (success) 
            {
                JOptionPane.showMessageDialog(this, "Question [ID: " + questionId + "] deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else 
            {
                JOptionPane.showMessageDialog(this, "Question ID " + questionId + " could not be found.", "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) 
        {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
}