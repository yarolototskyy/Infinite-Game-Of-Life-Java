import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.Timer;

/*
 * Displays generations of John Conway's Game of Life.
 * Allows a user of the program to step through one generation
 * at a time or to run the generations based on a timer.
 */
public class InfiniteGameOfLifeDisplay extends JFrame {

	/* 
	 * Display for the Infinite Game Board
	 * 
	 * This graphics console is mainly to show the functionality of the
	 * infinite board. It's missing quite a few features when compared to my
	 * non-infinite graphics console but I didn't really feel like re-implementing
	 * all that for this class too. The only way to set cells is through random set-up.
	 * 
	 * The two biggest features I added, however, are the zoom and pan functionality
	 * The user can zoom in and out of their current window to view more/less cells at once.
	 * Additionally, they can pan throughout the board to view a specific part of it.
	 * 
	 * */
	
	private JPanel contentPane;
	private JLabel txtGeneration = new JLabel();
	private JPanel boardPanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InfiniteGameOfLifeDisplay frame = new InfiniteGameOfLifeDisplay();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame. Adds a button panel to the frame and
	 * initializes the usage of each button.
	 */
	public InfiniteGameOfLifeDisplay() {
		InfiniteGameOfLife g = new InfiniteGameOfLife();// call an appropriate constructor
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		/*
		 * creates a Timer and defines what will occur when
		 * it is run when the user clicks the "start" button
		 */
		Timer timer = new Timer(10, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO what should happen each time the Timer is fired off
				g.nextGen();
				txtGeneration.setText("Generation: " + g.getGen());
				repaint();
			}
			
		});
		
		/*
		 * creates the button panel
		 */
		JPanel buttonPanel = new JPanel();
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		
		/*
		 * adds a button which allows the user to step through
		 * the game one generation at a time
		 */
		JButton nextGenButton = new JButton("Next Gen");
		buttonPanel.add(nextGenButton);
		nextGenButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO generate and display the next generation
				g.nextGen();
				repaint();
			}
			
		});
		
		/*
		 * creates a button that allows the game to run on 
		 * a timer. The label toggles between "Start" and "Stop"
		 */
		JButton startStopButton = new JButton("Start");
		buttonPanel.add(startStopButton);
		startStopButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(startStopButton.getText().equals("Start")){
					startStopButton.setText("Stop");
					// TODO start the generations 
					timer.start();
				}
				else{
					startStopButton.setText("Start");
					// TODO stop the generations
					timer.stop();
				}
				
			}
			
		});

		JButton zoomOutButton = new JButton("Zoom Out");
		buttonPanel.add(zoomOutButton);
		zoomOutButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if (((InfiniteBoardPanel)boardPanel).canZoomOut()) {
					int[] currentBounds = ((InfiniteBoardPanel) boardPanel).getBoundaries();
					int [] newBounds = getNewScaledBounds(currentBounds);
					((InfiniteBoardPanel) boardPanel).setBoundaries(newBounds);
					repaint();
				}
			}
			
			private int[] getNewScaledBounds(int[] bounds) {
				for (int bound : bounds) {
					if ((int)(bound * 1.1) == bound) {
						return new int[] {bounds[0] - 1, bounds[1] + 1, bounds[2] - 1, bounds[3] + 1};
					}
				}
				return new int[] {(int)(bounds[0] * 1.1), (int)(bounds[1] * 1.1), (int)(bounds[2] * 1.1), (int)(bounds[3] * 1.1)};
			}
			
		});
		
		JButton zoomInButton = new JButton("Zoom In");
		buttonPanel.add(zoomInButton);
		zoomInButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO generate and display the next generation
				int[] currentBounds = ((InfiniteBoardPanel) boardPanel).getBoundaries();
				int [] newBounds = new int[] {(int)(currentBounds[0] * 0.9), (int)(currentBounds[1] * 0.9), (int)(currentBounds[2] * 0.9), (int)(currentBounds[3] * 0.9)};
				((InfiniteBoardPanel) boardPanel).setBoundaries(newBounds);
				repaint();
			}
			
		});

		JButton randomSetupButton = new JButton("Random Set-up");
		buttonPanel.add(randomSetupButton);
		randomSetupButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int[] currentBounds = ((InfiniteBoardPanel) boardPanel).getSeenBoundaries();
				g.clear();
				g.randomSetup(currentBounds);
				if (timer.isRunning()) {
					timer.stop();
					startStopButton.setText("Start");
				}
				txtGeneration.setText("Generation: " + g.getGen());
				repaint();
			}
			
		});

		
		/*
		 * displays the generation number
		 */
		txtGeneration.setText("Generation: 0");
		contentPane.add(txtGeneration, BorderLayout.NORTH);
		txtGeneration.setHorizontalAlignment(JLabel.CENTER);
		
		/*
		 * adds the panel which displays the Game of Life
		 * board. See the BoardPanel class for details.
		 */
		boardPanel = new InfiniteBoardPanel(g);
		contentPane.add(boardPanel, BorderLayout.CENTER);
        MouseAdapter ma = new MouseAdapter() {

            private Point origin;
            private int[] currentBounds;
            @Override
            public void mousePressed(MouseEvent e) {
                origin = new Point(e.getPoint());
                currentBounds = ((InfiniteBoardPanel)boardPanel).getBoundaries();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
            	
                if (origin != null) {
            		int numSquaresX = currentBounds[1] - currentBounds[0] + 1;
            		int numSquaresY = currentBounds[3] - currentBounds[2] + 1;
            		int panelWidth = boardPanel.getWidth();
            		int panelHeight = boardPanel.getHeight();
            		int squareSideY, squareSideX;
            		squareSideX = panelWidth / numSquaresX;
            		squareSideY = panelHeight / numSquaresY;
                	int deltaX = (origin.x - e.getX()) / squareSideX;
                    int deltaY = (origin.y - e.getY()) / squareSideY;
                    ((InfiniteBoardPanel)boardPanel).setBoundaries(new int[] {currentBounds[0] + deltaX, currentBounds[1] + deltaX, currentBounds[2] + deltaY, currentBounds[3] + deltaY});
                    repaint();
                }
            }

        };
        boardPanel.addMouseListener(ma);
        boardPanel.addMouseMotionListener(ma);

		JButton clearButton = new JButton("Clear");
		buttonPanel.add(clearButton);
		clearButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO generate and display the next generation
				g.clear();
				if (timer.isRunning()) {
					timer.stop();
					startStopButton.setText("Start");
				}
				txtGeneration.setText("Generation: " + g.getGen());
				repaint();
			}
			
		});
		
	
	}
	


}
