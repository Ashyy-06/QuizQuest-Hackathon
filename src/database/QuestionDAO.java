package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import models.MultipleSelectMCQ;
import models.Question;
import models.SingleChoiceMCQ;
import models.TrueFalseQuestion;

public class QuestionDAO 
{

    public List<String> getAllTopics() 
    {
        List<String> topics = new ArrayList<>();
        String sql = "SELECT DISTINCT topic COLLATE NOCASE as unique_topic FROM questions ORDER BY unique_topic ASC";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try 
        {
            conn = DatabaseManager.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) 
            {
                String topic = rs.getString("unique_topic");
                if (topic != null && !topic.trim().isEmpty()) 
                {
                    String trimmed = topic.trim();
                    String formattedTopic = Character.toUpperCase(trimmed.charAt(0)) + trimmed.substring(1);
                    if (!topics.contains(formattedTopic)) 
                    {
                        topics.add(formattedTopic);
                    }
                }
            }
        } catch (SQLException e)
        {
            System.err.println("Error fetching topics: " + e.getMessage());
        } finally
        {
            try 
            {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e)
            {
                System.err.println("Failed to close topic query resources: " + e.getMessage());
            }
        }

        return topics;
    }

    public boolean addQuestion(String topic, String type, String questionText, String options, String correctAnswer) 
    {
        String sql = "INSERT INTO questions (topic, type, question_text, options, correct_answer) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try 
        {
            conn = DatabaseManager.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, topic);
            pstmt.setString(2, type);
            pstmt.setString(3, questionText);
            pstmt.setString(4, options);
            pstmt.setString(5, correctAnswer);

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) 
        {
            System.err.println("Error adding question: " + e.getMessage());
            return false;
        } finally 
        {
            try 
            {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) 
            {
                System.err.println("Failed to close statement: " + e.getMessage());
            }
        }
    }

    public List<Question> getAllQuestions() 
    {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try 
        {
            conn = DatabaseManager.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) 
            {
                Question q = mapRowToQuestion(rs);
                if (q != null) 
                {
                    questions.add(q);
                }
            }
        } catch (SQLException e) 
        {
            System.err.println("Error retrieving questions: " + e.getMessage());
        } finally 
        {
            try 
            {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) 
            {
                System.err.println("Failed to close resources: " + e.getMessage());
            }
        }

        return questions;
    }

    public List<Question> getQuestionsByTopic(String topic) 
    {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE LOWER(topic) = LOWER(?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try 
        {
            conn = DatabaseManager.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, topic.trim());
            rs = pstmt.executeQuery();

            while (rs.next()) 
            {
                Question q = mapRowToQuestion(rs);
                if (q != null) 
                {
                    questions.add(q);
                }
            }
        } catch (SQLException e) 
        {
            System.err.println("Error retrieving questions for topic " + topic + ": " + e.getMessage());
        } finally 
        {
            try 
            {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) 
            {
                System.err.println("Failed to close resources: " + e.getMessage());
            }
        }

        return questions;
    }

    public boolean deleteQuestion(int id) 
    {
        String sql = "DELETE FROM questions WHERE id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try 
        {
            conn = DatabaseManager.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) 
        {
            System.err.println("Error deleting question ID " + id + ": " + e.getMessage());
            return false;
        } finally 
        {
            try 
            {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) 
            {
                System.err.println("Failed to close statement: " + e.getMessage());
            }
        }
    }

    public void seedDefaultQuestions() 
    {
        String checkSql = "SELECT COUNT(*) FROM questions";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try 
        {
            conn = DatabaseManager.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(checkSql);

            if (rs.next() && rs.getInt(1) == 0) 
            {
                addQuestion("Science", "MCQ", "The chemical reaction between copper and oxygen can be categorized as:", 
                "Displacement reaction|Decomposition reaction|Combination reaction|Double displacement reaction", "3");
                addQuestion("Science", "MCQ", "Opening and closing of stomatal pore depends on:", 
                "Atmospheric temperature|Oxygen concentration around stomata|Carbon dioxide concentration around stomata|Water content in the guard cells", "4");
                addQuestion("Science", "MCQ", "Which of the following mirror is used by a dentist to examine a small cavity in a patient's teeth?", 
                "Convex mirror|Plane mirror|Concave mirror|Any spherical mirror", "3");
                addQuestion("Science", "MCQ", "Which of the following can make a parallel beam of light when light from a point source is incident on it?", 
                "Concave mirror as well as convex lens|Convex mirror as well as concave lens|Two plane mirrors placed at 90 degrees|Concave mirror as well as concave lens", "1");
                addQuestion("Science", "MCQ", "In which group of organisms does blood flow through the heart only once during one cycle?", 
                "Rabbit, Parrot, Turtle|Frog, crocodile, Pigeon|Whale, Labeo, Penguin|Shark, dog fish, sting ray", "4");
                addQuestion("Science", "MCQ", "What is common between blood vessels around alveoli walls and glomerulus of nephron?", 
                "Thick walled arteries poorly supplied|Thin walled veins poorly supplied|Thick walled capillaries poorly supplied|Thin walled capillaries richly supplied with blood", "4");
                addQuestion("Science", "MCQ", "Which one of the following processes is NOT followed by plants for excretion?", 
                "Get rid of excess water by transpiration|Selectively filter toxic substances through leaves|Waste products stored as resins and gums|Excrete waste substances into soil", "2");
                addQuestion("Science", "MCQ", "In which organisms is food material broken down outside the body and then absorbed?", 
                "Mushroom, green plants, amoeba|Yeast, mushroom, bread mould|Paramecium, amoeba, cuscuta|Cuscuta, lice, tapeworm", "2");
                addQuestion("Science", "MCQ", "If the tubule part of the nephron is not functioning, what is its effect on urine formation?", 
                "Urine will not be formed|Quality and quantity unaffected|Urine is more concentrated|Urine is more diluted", "4");
                addQuestion("Science", "MCQ", "What is the saturated solution of Sodium Chloride called?", 
                "Brine|Lime water|Slaked lime|Soda water", "1");
                addQuestion("Science", "TRUE_FALSE", "Concave mirrors always form virtual and erect images regardless of object position.", "", "false");
                addQuestion("Science", "TRUE_FALSE", "Transpiration in plants aids in temperature regulation and water transport.", "", "true");

                addQuestion("Science", "MULTI_SELECT", "Which of the following are greenhouse gases? (Select all that apply)",
                "Carbon Dioxide|Methane|Argon|Nitrous Oxide", "1,2,4");
                addQuestion("Science", "MULTI_SELECT", "Which of the following organisms exhibit saprotrophic nutrition? (Select all that apply)",
                "Yeast|Bread mould|Amoeba|Mushroom", "1,2,4");

                addQuestion("Social Science", "MCQ", "Who among the following was the founder of 'Young Europe' secret society in Berne?", 
                "Giuseppe Garibaldi|Giuseppe Mazzini|Count Cavour|Otto von Bismarck", "2");
                addQuestion("Social Science", "MCQ", "Which of the following was the result of the Act of Union, 1707?", 
                "Unification of Germany|Unification of Great Britain|Unification of Italy|Unification of Vietnam", "2");
                addQuestion("Social Science", "MCQ", "Identify the correct statement with regard to the 'Zollverein'.", 
                "Coalition to manage political alliances|Formed to bind Prussia politically|Customs Union at the initiative of Prussia|Awakened national sentiment in Europe", "3");
                addQuestion("Social Science", "MCQ", "Which countries were involved in the Three Wars with Prussia?", 
                "Austria, Poland and France|Austria, Denmark and France|Austria, Turkey and France|Austria, England and France", "2");
                addQuestion("Social Science", "MCQ", "Identify the ideology under which people demanded freedom of markets in 19th-century Europe.", 
                "Romanticism|Liberalism|Socialism|Rationalism", "2");
                addQuestion("Social Science", "MCQ", "Choose the correctly matched pair about crops and where they are grown.", 
                "Groundnut - Assam|Tea - Gujarat|Coffee - Karnataka|Sugarcane - Chhattisgarh", "3");
                addQuestion("Social Science", "MCQ", "The piece of land left uncultivated for the past 1 to 5 agricultural years is called:", 
                "Barren land|Forest land|Grazing land|Fallow land", "4");
                addQuestion("Social Science", "MCQ", "Which conference was convened in 1992 to discuss environmental protection globally?", 
                "Kyoto Protocol|Montreal Protocol|Rio de Janeiro Earth Summit|World Summit on Sustainable Development", "3");
                addQuestion("Social Science", "MCQ", "Which human activity has contributed significantly to land degradation?", 
                "Deforestation|Crop rotation|Shelter belts|Ploughing", "1");
                addQuestion("Social Science", "MCQ", "Which country is an example of a 'Coming Together Federation'?", 
                "India|Sri Lanka|USA|Belgium", "3");
                addQuestion("Social Science", "TRUE_FALSE", "Sri Lanka adopted majoritarianism in its Constitution.", "", "true");
                addQuestion("Social Science", "TRUE_FALSE", "The courier service belongs to the secondary sector of the economy.", "", "false");

                addQuestion("Social Science", "MULTI_SELECT", "Which of the following countries are considered 'Coming Together' Federations? (Select all that apply)",
                "USA|Switzerland|Australia|India", "1,2,3");
                addQuestion("Social Science", "MULTI_SELECT", "Which of the following are primary sector activities? (Select all that apply)",
                "Agriculture|Dairy farming|Banking|Fishing", "1,2,4");

                System.out.println("Default questions seeded successfully!");
            }
        } catch (SQLException e) 
        {
            System.err.println("Error checking question count: " + e.getMessage());
        } finally 
        {
            try 
            {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) 
            {
                System.err.println("Failed to close seed resources: " + e.getMessage());
            }
        }
    }

    public List<Question> getRandomQuestionsByTopic(String topic, int limit) 
    {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE LOWER(topic) = LOWER(?) ORDER BY RANDOM() LIMIT ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try 
        {
            conn = DatabaseManager.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, topic.trim());
            pstmt.setInt(2, limit);
            rs = pstmt.executeQuery();

            while (rs.next()) 
            {
                Question q = mapRowToQuestion(rs);
                if (q != null) 
                {
                    questions.add(q);
                }
            }
        } catch (SQLException e) 
        {
            System.err.println("Error retrieving random questions for topic " + topic + ": " + e.getMessage());
        } finally 
        {
            try 
            {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) 
            {
                System.err.println("Failed to close resources: " + e.getMessage());
            }
        }

        return questions;
    }

    private Question mapRowToQuestion(ResultSet rs) throws SQLException 
    {
        String idStr = String.valueOf(rs.getInt("id"));
        String type = rs.getString("type");
        String prompt = rs.getString("question_text");
        String options = rs.getString("options");
        String answer = rs.getString("correct_answer");
        int points = 1; 

        if ("MCQ".equalsIgnoreCase(type)) 
        {
            List<String> optionList = new ArrayList<>();
            if (options != null && !options.trim().isEmpty()) 
            {
                optionList = Arrays.asList(options.split("\\|"));
            }

            int correctIndex = 1;
            try 
            {
                correctIndex = Integer.parseInt(answer.trim());
            } catch (NumberFormatException ignored) {}

            return new SingleChoiceMCQ(idStr, prompt, points, optionList, correctIndex);

        } else if ("TRUE_FALSE".equalsIgnoreCase(type)) 
        {
            boolean correctBool = Boolean.parseBoolean(answer.trim());
            return new TrueFalseQuestion(idStr, prompt, points, correctBool);

        } else if ("MULTI_SELECT".equalsIgnoreCase(type)) 
        {
            List<String> optionList = new ArrayList<>();
            if (options != null && !options.trim().isEmpty()) 
            {
                optionList = Arrays.asList(options.split("\\|"));
            }

            Set<Integer> correctIndices = new HashSet<>();
            if (answer != null && !answer.trim().isEmpty()) 
            {
                String[] parts = answer.split(",");
                for (String part : parts) 
                {
                    try 
                    {
                        correctIndices.add(Integer.parseInt(part.trim()));
                    } catch (NumberFormatException ignored) {}
                }
            }

            return new MultipleSelectMCQ(idStr, prompt, points, optionList, correctIndices);
        }

        return null;
    }
}