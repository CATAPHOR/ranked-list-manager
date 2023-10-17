import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Class built to handle ranked lists and operations on them.
 * @author CATAPHOR
 *
 */
public class ListMaker 
{
	/*
	 * INTERNAL list-related
	 */
	/**
	 * internal list, holding all ranked (by order) items
	 */
	private ArrayList<String> list;
	
	/*
	 * FILE HANDLING
	 */
	/**
	 * regex pattern to validate read textual content
	 */
	private String pattern;
	/**
	 * holds each line of text file
	 */
	private ArrayList<String> textFileLines;
	/**
	 * File from which data is read, and to which data is to be stored
	 */
	private File inputFile, outputFile;
	/**
	 * prints to file when saving content
	 */
	private PrintStream printStream;
	
	/*
	 * CONSTRUCTORS
	 */
	/**
	 * Default constructor, will generate empty list and set files to null.
	 */
	public ListMaker()
	{
		this.list = new ArrayList<String>();
		this.setInputFile(null);
		this.setOutputFile(null);
		
		//regex pattern matches: [BOF][numerical]+["."][whitespace]+[any character]+
		this.pattern = "^[0-9]+\\.\\s+.+";
	}
	
	/**
	 * If file given, will attempt to populate internal list with any textual data held therein
	 * @param file Text file holding data pertaining to ranked list
	 */
	public ListMaker(File file)
	{
		//use default constructor to initialise empty list and regex pattern
		this();
		
		//attempt reading from file
		try
		{
			//set input file
			this.inputFile = file;
			//by default, set output file to input
			this.outputFile = file;
			
			//read lines from text file
			this.textFileLines = this.getFileContent(this.inputFile);
			//populate internal list from strings in textFileLines
			this.list = this.textLinesToList(this.textFileLines);
		}
		catch (Exception ex)
		{
			
		}
	}
	
	/**
	 * Sets file to read list from.
	 * @param file Text file holding ranked list.
	 */
	public void setInputFile(File file)
	{
		this.inputFile = file;
	}
	
	/**
	 * Input file getter.
	 * @return Input file.
	 */
	public File getInputFile()
	{
		return this.inputFile;
	}
	
	/**
	 * Sets file to save list to.
	 * @param file Text file to which list will be saved.
	 */
	public void setOutputFile(File file)
	{
		this.outputFile = file;
	}

	/**
	 * Output file getter.
	 * @return Output file
	 */
	public File getOutputFile()
	{
		return this.outputFile;
	}
	
	/**
	 * Returns current internal list.
	 * @return ArrayList of String entries in list.
	 */
	public ArrayList<String> getList()
	{
		return this.list;
	}
	
	/**
	 * Returns list-formatted ArrayList of ranked items.
	 * @param input lines of text from file containing list data
	 * @return ArrayList of entries in ranked order
	 */
	private ArrayList<String> textLinesToList(ArrayList<String> input)
	{
		ArrayList<String> output = new ArrayList<String>();
		
		for (String line : input)
		{
			//identify valid lines with pattern string
			if (Pattern.matches(pattern, line))
			{
				//add rank entry to array
				output.add(line.replaceFirst("^[0-9]+\\.\\s+", ""));
			}
		}
		
		return output;
	}
	
	/**
	 * Gets all lines stored in specified text file.
	 * @param file text file storing list data
	 * @return ArrayList containing each line as a String 
	 * @throws Exception
	 */
	private ArrayList<String> getFileContent(File file) throws Exception
	{
		//create input stream
		BufferedReader reader = new BufferedReader(new FileReader(file));
		//ArrayList to store all lines of text file
		ArrayList<String> fileContent = new ArrayList<String>();
		
		//read through all lines, adding each to the ArrayList
		while (reader.ready())
		{
			fileContent.add(reader.readLine());
		}
		
		//close input stream
		reader.close();
		
		return fileContent;
	}
	
	/**
	 * Adds item to list at specified rank/position.
	 * @param pos rank to place entry at (indexed from 1)
	 * @param entry item to add at specified rank
	 */
	public void addEntry(int pos, String entry)
	{
		entry = entry.toLowerCase();
		
		if (pos > 0 && pos <= this.list.size() + 1)
		{
			this.list.add(pos - 1, entry);
		}
	}
	
	/**
	 * Removes an item from list by its rank/position.
	 * @param pos rank of item to be removed (indexed from 1)
	 */
	public void removeEntry(int pos)
	{
		if (pos > 0 && pos <= this.list.size())
		{
			this.list.remove(pos - 1);
		}
	}
	
	/**
	 * Removes an item from list by its entry name.
	 * @param entry Name of entry in list to remove
	 */
	public void removeEntry(String entry)
	{
		entry = entry.toLowerCase();
		
		if (this.list.contains(entry))
		{
			this.list.remove(entry);
		}
	}
	
	/**
	 * Changes the name of an entry at a certain rank on the list.
	 * @param pos rank of entry on list (indexed from 1)
	 * @param newEntry name to change entry to
	 */
	public void changeEntry(int pos, String newEntry)
	{
		if (pos > 0 && pos <= this.list.size())
		{
			this.list.remove(pos - 1);
			this.list.add(pos - 1, newEntry);
		}
	}
	
	/**
	 * Changes the name of an entry (given by name) on the list.
	 * @param oldEntry name of entry to change
	 * @param newEntry name to change entry to
	 */
	public void changeEntry(String oldEntry, String newEntry)
	{
		if (this.list.contains(oldEntry))
		{
			int pos = this.list.indexOf(oldEntry);
			this.list.remove(oldEntry);
			this.list.add(pos, newEntry);
		}
	}
	
	/**
	 * Move an entry on the list to the specified position.
	 * @param entry Name of entry to move.
	 * @param toPos Position on list to place entry. (indexed from 1)
	 */
	public void moveEntry(String entry, int toPos)
	{
		entry = entry.toLowerCase();
		
		if (this.list.contains(entry) && 
				toPos > 0 && toPos <= this.list.size() + 1)
		{
			this.list.remove(entry);
			this.list.add(toPos - 1, entry);
		}
	}
	
	/**
	 * Move the entry at the specified position to a new position.
	 * @param fromPos Position of entry to move. (indexed from 1)
	 * @param toPos	Position to which entry is to be moved. (indexed from 1)
	 */
	public void moveEntry(int fromPos, int toPos)
	{
		if (fromPos > 0 && fromPos <= this.list.size() &&
				toPos > 0 && toPos <= this.list.size() + 1)
		{
			String entry = this.list.get(fromPos - 1);
			this.list.remove(fromPos - 1);
			this.list.add(toPos - 1, entry);
		}
	}
	
	/**
	 * Writes internal list content to text file
	 */
	public void writeToTextFile()
	{
		if (this.outputFile != null)
		{
			try 
			{
			      this.printStream = new PrintStream(this.outputFile);
			      for (int i = 0; i < this.list.size(); i++)
			      {
			    	  this.printStream.println((i + 1) + ".\t" + this.list.get(i));
			      }
			      this.printStream.close();
			}
			catch (IOException e)
			{
				
			}
		}
	}
	
	/**
	 * Prints current list, ranked, to stdout
	 */
	public void printList()
	{
		for (int i = 0; i < this.list.size(); i++)
		{
			System.out.println((i + 1) + ".\t" + this.list.get(i));
		}
	}
	
	/*
	 * TESTING
	 */
	public static void main(String[] args)
	{
		
		ArrayList<String> test = new ArrayList<String>();
		test.add("test");
		test.add("test2");
		test.add("test3");
		test.remove("test2");
		System.out.println(Arrays.toString(test.toArray()));
	}
}
