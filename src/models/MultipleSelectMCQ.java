package models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MultipleSelectMCQ extends Question 
{
    private List<String> options;
    private Set<Integer> correctOptionIndices; 

    public MultipleSelectMCQ(String id, String prompt, int points, List<String> options, Set<Integer> correctOptionIndices) 
    {
        super(id, prompt, points);
        this.options = options;
        this.correctOptionIndices = correctOptionIndices != null ? correctOptionIndices : new HashSet<>();
    }

    public List<String> getOptions() 
    {
        return options;
    }

    public Set<Integer> getCorrectOptionIndices() 
    {
        return correctOptionIndices;
    }

    @Override
    public void displayQuestion() 
    {
        System.out.println(getPrompt() + " (Select all that apply)");
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
    }

    @Override
    public boolean evaluateAnswer(Object userAnswer) 
    {
        if (!(userAnswer instanceof Set<?>)) 
        {
            return false;
        }
        Set<?> userSelections = (Set<?>) userAnswer;
        return correctOptionIndices.equals(userSelections);
    }
}