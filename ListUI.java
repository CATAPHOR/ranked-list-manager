import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;

public class ListUI extends JFrame //implements UpdateListener?
{
	private static final long serialVersionUID = -511580737425516299L;
	
	private JPanel contentPane;
	private MainPanel mainPanel;
	private ListMaker listmaker;
	JFileChooser chooser;
	
	public ListUI()
	{
		super("List Maker");
		
		//try to set look and feel to system default (avoid system-agnostic java app aesthetic)
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception ex)
		{
			
		}
		
		//get file
		JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(new JFrame());
	    //initialise listmaker from file, or from no file
	    if	(returnVal == JFileChooser.APPROVE_OPTION) 
	    {
	    	this.listmaker = new ListMaker(chooser.getSelectedFile());
	    }
	    else
	    {
	    	this.listmaker = new ListMaker();
	    }
	    
		//Display window
		setSize(1000,600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//set top level content pane
		this.contentPane = new JPanel();
		this.setContentPane(this.contentPane);
		this.contentPane.setBorder(new EmptyBorder(5, 5, 10, 5));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		
		//create mainpanel, add to contentpane
		this.mainPanel = new MainPanel();
		this.contentPane.add(mainPanel);
		
		setVisible(true);
	}
	
	private class MainPanel extends JPanel
	{
		private static final long serialVersionUID = -9008190451862932154L;
		
		protected JScrollPane scrollPane;
		protected JTable table;
		protected ArrayList<JButton> buttons;
		protected JPanel buttonPanel;
		
		public MainPanel()
		{
			//super(), set layout
			super();
			this.setLayout(new BorderLayout());
			
			
			//create scrollpane
			this.scrollPane = new JScrollPane();
			//create table
			this.tableGen(new ListTableModel());
			
			//generate buttons
			this.buttonPanel = new JPanel();
			this.buttonPanel.setBorder(new EmptyBorder(0, 5, 5, 5));
			this.buttons = new ArrayList<JButton>();
			this.addButtons(new String[] {"Open", "New", "Add", "Edit", "Move", "Remove", "Save", "Save As"});
			// TODO map for button names?
			this.giveButtonsFunctionality();
			
			//initiate all GUI elements
			this.guiInit();
		}
		
		/**
		 * Initialises all GUI compenents
		 */
		protected void guiInit()
		{
			//scroll pane
			this.scrollPane.getViewport().add(this.table);
			this.add(this.scrollPane, BorderLayout.CENTER);
			
			//buttons
			this.add(this.buttonPanel, BorderLayout.NORTH);
		}
		
		protected void refreshTable()
		{
			try
			{
				int[] r = this.table.getSelectedRows();
				this.table.setModel(new ListTableModel());
				//ensure user is still selecting the same number of rows
//				if (r.length > 0)
//				{
//					this.table.addRowSelectionInterval(r[0], r[r.length - 1]);
//				}
			}
			catch (IllegalArgumentException ex)
			{
				
			}
		}
		
		protected void giveButtonsFunctionality()
		{
			//TODO
			//OPEN
			this.buttons.get(0).addActionListener(g -> 
			{
				if (true)// TODO size 0; input/output file (figure this one out, this.changed bool?))
				{
					int option = JOptionPane.showConfirmDialog(null, "Ensure current table is saved first, if necessary.",
							"Open new list?", JOptionPane.OK_CANCEL_OPTION);
					if (option == JOptionPane.OK_OPTION)
					{
						JFileChooser chooser = new JFileChooser();
					    FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
					    chooser.setFileFilter(filter);
					    int returnVal = chooser.showOpenDialog(new JFrame());
					    
					    //initialise listmaker from file, or from no file
					    if	(returnVal == JFileChooser.APPROVE_OPTION) 
					    {
					    	ListUI.this.listmaker = new ListMaker(chooser.getSelectedFile());
					    	this.refreshTable();
					    }
					    else {}
					}
				}
			});
			
			//NEW
			this.buttons.get(1).addActionListener(g -> 
			{
				if (ListUI.this.listmaker.getList().size() > 0)
				{
					int option = JOptionPane.showConfirmDialog(null, "Ensure current table is saved first, if necessary.",
							"Create new list?", JOptionPane.OK_CANCEL_OPTION);
					if (option == JOptionPane.OK_OPTION)
					{
						ListUI.this.listmaker = new ListMaker();
						this.refreshTable();
					}
				}
			});
			
			//ADD
			this.buttons.get(2).addActionListener(g ->
			{
				//user entry text field
				JTextField entry = new JTextField();
				JTextField rank = new JTextField();
				Object[] message = {
						"Entry name:", entry,
						"Rank:", rank
				};
				
				//add window
				int option = JOptionPane.showConfirmDialog(null, message, "Add new entry:", 
						JOptionPane.OK_CANCEL_OPTION);
				if (option == JOptionPane.OK_OPTION)
				{
					try
					{
						//get name of new entry
						String addEntry = entry.getText();
						if (ListUI.this.listmaker.getList().contains(addEntry))
						{
							throw new Exception("Entry already in list!");
						}
						if (addEntry.equals(""))
						{
							throw new Exception("Please enter a valid name.");
						}
						
						//get position/rank of new entry
						int pos = Integer.parseInt(rank.getText());
						if (pos < 1 || pos > ListUI.this.listmaker.getList().size() + 1)
						{
							throw new Exception("Rank must be between 1 and " + 
									(ListUI.this.listmaker.getList().size() + 1));
						}
						
						ListUI.this.listmaker.addEntry(pos, addEntry);
						this.refreshTable();
					}
					catch (NumberFormatException ex)
			    	{
			    		JOptionPane.showMessageDialog(null,
								"Please enter valid number for new rank.",
							    "Edit Error",
							    JOptionPane.ERROR_MESSAGE);
			    	}
					catch (Exception ex)
					{
						JOptionPane.showMessageDialog(null,
							    ex.getMessage(),
							    "Edit Error",
							    JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			
			//EDIT
			this.buttons.get(3).addActionListener(g -> 
			{
				if (this.table.getSelectedRows().length > 0)
				{
					//user entry text field
					JTextField entry = new JTextField(
							this.table.getValueAt(this.table.getSelectedRow(), 1).toString());
					Object[] message = {
							"Entry name:", entry
					};
					
					//edit window
					int option = JOptionPane.showConfirmDialog(null, message, "Change rank " + 
							this.table.getValueAt(this.table.getSelectedRow(), 0).toString() + " entry:", 
							JOptionPane.OK_CANCEL_OPTION);
					if (option == JOptionPane.OK_OPTION)
					{
						try
						{
							//get new entry name
							String newEntry = entry.getText();
							if (newEntry.equals(""))
							{
								throw new Exception("Please enter valid text.");
							}
							
							//change list accordingly
							int pos = (int) this.table.getValueAt(this.table.getSelectedRow(), 0);
							ListUI.this.listmaker.changeEntry(pos, newEntry);
							
							//refresh table contents
							this.refreshTable();
							//preserve selection
							this.table.addRowSelectionInterval(pos - 1, pos - 1);
						}
						catch (Exception ex)
						{
							JOptionPane.showMessageDialog(null,
								    ex.getMessage(),
								    "Edit Error",
								    JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
			
			//MOVE
			this.buttons.get(4).addActionListener(g -> 
			{
				if (this.table.getSelectedRows().length > 0)
				{
					//user entry text field
					JTextField entry = new JTextField(
							this.table.getValueAt(this.table.getSelectedRow(), 0).toString());
					Object[] message = {
							"New Rank:", entry
					};
					
					//edit window
					int option = JOptionPane.showConfirmDialog(null, message, "Change rank of \"" + 
							this.table.getValueAt(this.table.getSelectedRow(), 1).toString() + "\":", 
							JOptionPane.OK_CANCEL_OPTION);
					if (option == JOptionPane.OK_OPTION)
					{
						try
						{
							//get new entry name
							int newRank = Integer.parseInt(entry.getText());
							if (newRank < 1 || newRank > ListUI.this.listmaker.getList().size())
							{
								throw new Exception("Rank must be between 1 and " + 
										ListUI.this.listmaker.getList().size());
							}
							
							//change list accordingly
							ListUI.this.listmaker.moveEntry((int) this.table.getValueAt(this.table.getSelectedRow(), 0), 
									newRank);
							
							//refresh table contents
							this.refreshTable();
							//change selection to row at new rank position
							this.table.clearSelection();
							this.table.addRowSelectionInterval(newRank - 1, newRank - 1);
						}
						catch (NumberFormatException ex)
				    	{
				    		JOptionPane.showMessageDialog(null,
									"Please enter valid number for new rank.",
								    "Edit Error",
								    JOptionPane.ERROR_MESSAGE);
				    	}
						catch (Exception ex)
						{
							JOptionPane.showMessageDialog(null,
								    ex.getMessage(),
								    "Edit Error",
								    JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
			
			//REMOVE
			this.buttons.get(5).addActionListener(g -> 
			{ 
				try
				{
					//only proceed if entry highlighted
					if (this.table.getSelectedRow() == -1)
					{
						throw new ArrayIndexOutOfBoundsException();
					}
					
					//delete all selected rows
					int rows[] = this.table.getSelectedRows();
					int index = 0;
					while (index < rows.length)
					{
						ListUI.this.listmaker.removeEntry(rows[index++] + 1);
						
						//shift back each row number as an entry has been deleted
						for (int i = index; i < rows.length; i++)
						{
							rows[i] -= 1;
						}
					}
					
					//refresh table contents
					this.refreshTable();
				}
//				catch (UnableToDeleteException ex)
//				{
//					JOptionPane.showMessageDialog(null,
//							ex.getMessage(),
//						    "Delete Error",
//						    JOptionPane.ERROR_MESSAGE);
//				}
				//TODO do we even need
				catch (ArrayIndexOutOfBoundsException ex)
				{
					JOptionPane.showMessageDialog(null,
						    "No entry selected.",
						    "Delete Error",
						    JOptionPane.ERROR_MESSAGE);
				}
			});
			
			//TODO
			//SAVE
			//old version, check if equal
			this.buttons.get(6).addActionListener(g ->
			{
				if (ListUI.this.listmaker.getOutputFile() != null)
				{
					ListUI.this.listmaker.writeToTextFile();
					// TODO exception
					JOptionPane.showMessageDialog(null, "List saved successfully.", 
							"", JOptionPane.INFORMATION_MESSAGE);
				}
				else
				{
					// TODO save as func
				}
			});
			
			// TODO
			//SAVE AS
		}
		
		//method to add buttons to bottom of panel
		protected void addButtons(String[] names)
		{
			this.buttonPanel.setLayout(new GridLayout(0, names.length, 10, 10));

			for (int i = 0; i < names.length; i++)
			{
				this.buttons.add(new JButton(names[i]));
				this.buttonPanel.add(this.buttons.get(i));
			}
		}
				
		/**
		 * Generate table (list).
		 * @param m Table model to initialise table.
		 */
		protected void tableGen(ListTableModel m)
		{
			this.table = new JTable(m);
			//TODO resize first column somehow
			this.table.setShowGrid(false);
			this.table.setDefaultEditor(Object.class, null);
			this.table.setFillsViewportHeight(true);
		}
		
		/**
		 *	Specifies table model for displaying a list.
		 */
		protected class ListTableModel extends AbstractTableModel
		{
			private static final long serialVersionUID = -5641981377432262941L;
			
			protected String[] columnHeaders;
			protected ArrayList<Object[]> data = new ArrayList<Object[]>();
			
			public ListTableModel()
			{
				this.columnHeaders = new String[] {"Rank", "Entry"};
				for (int i = 0; i < ListUI.this.listmaker.getList().size(); i++)
				{
					this.data.add(new Object[] {i + 1, ListUI.this.listmaker.getList().get(i)});
				}
			}
			
			public int getColumnCount() 
			{
				return this.columnHeaders.length;
			}
			
			public String getColumnName(int i)
			{
				return this.columnHeaders[i];
			}
			
			public int getRowCount()
			{
				return this.data.size();
			}

			public Object getValueAt(int i, int j) 
			{
				return this.data.get(i)[j];
			}
			
			public void setValueAt(Object o, int i, int j)
			{
				this.data.get(i)[j] = o;
				this.fireTableCellUpdated(i, j);
			}
			
			public boolean isCellEditable(int i, int j)
			{
				return false;
			}
		}
	}
	
	//TODO is this necessary idk
//	public void updated(UpdateEvent updateEvent) 
//	{
//		this.mainPanel.refreshTable();
//	}
	
	public static void main(String[] args)
	{
		new ListUI();
	}
}
