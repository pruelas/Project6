
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public final class TicTacToeClient {

    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("codebank.xyz", 38006)) {
            System.out.println("Connected to server.");

            //Used to create a thread that will solely read the incoming messages from the server 
            Runnable rn = () -> {
            try{

              InputStream is = socket.getInputStream();
              ObjectInputStream in = new ObjectInputStream(is);

              //Stored object sent by server
              Message mS = (Message)in.readObject(); 

              //Loop that will contitnue running as long as the server is sending messages
              //and the game hasn't ended
              while(mS != null){
              
                //Statement executes and prints out the current state of the game
                if(mS.getType() == MessageType.BOARD){
 

                  //Prints out board
                  BoardMessage bM = (BoardMessage) mS;

                  byte[][] b = bM.getBoard();
  
                  System.out.println("\n\nCurrent state of the board");

                  System.out.println("      0      1      2");
                  System.out.println("      _      _      _");

                  for(int i = 0; i < b.length; i++){
                
                    System.out.print(i+ " |");
                    for(int j = 0; j < b.length; j++){
                      char op = ' ';
                      if(b[i][j] == 0) 
                        op =  '*' ;
                      else if(b[i][j] == 1)
                        op = 'X';
                      else 
                        op = 'O';
                    
                    System.out.print("   " +op + "   ");
                   }
                   System.out.println("\n");
                }
              
              BoardMessage.Status status = bM.getStatus();
         
              //Looks at the status set by the server and prints the corresponding message 
              switch(status){
                case ERROR:
                  System.out.println("Error occurred");
                  System.out.println("Disconnected");
                  System.exit(0);
                  break;
                     
                case PLAYER1_SURRENDER:
                  System.out.println("Player 1 surrendered.");
                  System.out.println("Disconnected");
                  System.exit(0);
                  break;
                         
                case PLAYER2_SURRENDER:
                  System.out.println("Player 2 surrendered.");
                  System.out.println("Disconnected");
                  System.exit(0);
                  break;

                case PLAYER1_VICTORY:
                  System.out.println("Player 1 won.");
                  System.out.println("Disconnected");
                  System.exit(0);
                  break;
                    
                case PLAYER2_VICTORY:
                  System.out.println("Player 2 won.");
                  System.out.println("Disconnected");
                  System.exit(0);
                  break;
                       
                case STALEMATE:
                  System.out.println("\nGame resulted in a stalemate.");
                  System.out.println("Disconnected");
                  System.exit(0);
                  break;
                             
                default:
                  System.out.println();
                  break;
              }
               mS = (Message)in.readObject(); 

            }else if(mS.getType() == MessageType.ERROR){
              System.out.println("Error occurred");
              System.out.println("Disconnected");
              System.exit(0);
            }        

            }
            }catch (Exception e){
             System.out.println(e);
            }
            };

            //Initialize an output stream to communicate with the server
            OutputStream os = socket.getOutputStream();
            PrintStream out = new PrintStream(os, true, "UTF-8");     

            //Read input from the user
            Scanner sc = new Scanner(System.in);

            //Listening thread is started
            Thread listening = new Thread(rn);
            listening.start();  

            System.out.println("Tic Tac Toe"); 
            System.out.println("Please enter your username:");   
            String user = sc.nextLine();

            //Create a connect message to start the game
            ConnectMessage cM = new ConnectMessage(user);

            //Send connect message to server
            ObjectOutputStream oOS = new ObjectOutputStream(os);
            oOS.writeObject(cM);

            System.out.println("\nA new game was started."); 
            System.out.println("Your squares held will be represented by an X and the sqaures "
                         + "held by the server will by represented by an O.");        

            CommandMessage cMMD = new CommandMessage(CommandMessage.Command.NEW_GAME);
            oOS.writeObject(cMMD);


            //Used to try to make the board appear before asking the user for the row and column values
            Thread t = Thread.currentThread();
            t.sleep(3000);
            
            while(true){
            System.out.println("Please enter the row and column values of the spot on the board that you will like to hold.");
            System.out.println("Please enter the row value: ");
            byte row = sc.nextByte();

            System.out.println("Please enter the column value: ");
            byte col = sc.nextByte();

            MoveMessage mM = new MoveMessage(row,col);

            oOS.writeObject(mM);
            t.sleep(3000);

            }  

      }
    }
  
}















