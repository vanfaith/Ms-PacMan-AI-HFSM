package entrants.pacman.username;

import java.util.ArrayList;
import java.util.Random;

import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Constants;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getMove() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., entrants.pacman.username).
 */
public class MyPacMan extends PacmanController {
    private MOVE myMove = MOVE.LEFT;
    public enum PlayerState {EAT,ATTACK,WONDER,RUN};
    PlayerState actualPlayerState = PlayerState.EAT;
    
    private Random random = new Random();
   // private Constants.GHOST EvadingGhost;
 
    public boolean doIseePills(Game game) {
    	// function that returns true if a pill or a Power pill is visible
    	int[] pills = game.getPillIndices();
        int[] powerPills = game.getPowerPillIndices();

        for (int i = 0; i < pills.length; i++) {
            //check which pills are available
            Boolean pillStillAvailable = game.isPillStillAvailable(i);
            if (pillStillAvailable != null) {
                if (pillStillAvailable) {
                    return true;
                }
            }
        }

        for (int i = 0; i < powerPills.length; i++) {            //check with power pills are available
            Boolean pillStillAvailable = game.isPillStillAvailable(i);
            if (pillStillAvailable != null) {
                if (pillStillAvailable) {
                    return true;
                }
            }
        }
    	return false;
    }
    
    
    public boolean doIseeEdibleGhost(Game game) {
    	//This Returns True if Ms Pacman can see an edible ghost 
    for (Constants.GHOST ghost : Constants.GHOST.values()) {
        // If it is > 0 then it is visible so no more PO checks
        if (game.getGhostEdibleTime(ghost) > 0) {
            int distance = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghost));
            if (distance < 20) {
            	return true;
            }
        }
    }
    return false;
    }
    
    
    
    
    
    
    public MOVE getMove(Game game, long timeDue) {
        //Place your game logic here to play the game as Ms Pac-Man
        // Should always be possible as we are PacMan
        int current = game.getPacmanCurrentNodeIndex();
        //int invadingGhostLocation=0;
        if(game.wasPacManEaten()) {actualPlayerState= PlayerState.EAT;}//initial state
        
        System.out.println(actualPlayerState);
        System.out.println(current);
        int minDistanceGhost = Integer.MAX_VALUE;
        Constants.GHOST minGhost1 = null;
        
        MOVE[] moves = game.getPossibleMoves(current, game.getPacmanLastMoveMade());
        //check for invaders
        for (Constants.GHOST ghost : Constants.GHOST.values()) {
            // If can't see these will be -1 so all fine there
            if (game.getGhostEdibleTime(ghost) == 0 && game.getGhostLairTime(ghost) == 0) {
                int ghostLocation = game.getGhostCurrentNodeIndex(ghost);
                if (ghostLocation != -1) {
                	int distance = game.getShortestPathDistance(current, ghostLocation);
                    if (distance < 50 && distance < minDistanceGhost) {
                        //Vres to pio kontino fantasma
                    	minDistanceGhost = distance; //go after closer edible ghost
                        minGhost1 = ghost;
                    }
                }
            }
            if((game.wasPowerPillEaten()||game.getGhostEdibleTime(ghost) > 40 && game.getGhostLairTime(ghost) == 0)) {
            actualPlayerState= PlayerState.ATTACK;
            System.out.println("Power Pill active");}
            
        }
        
        if (minGhost1 != null) {
        	actualPlayerState= PlayerState.RUN;
          return myMove =game.getNextMoveAwayFromTarget(current, game.getGhostCurrentNodeIndex(minGhost1) ,Constants.DM.PATH);
      }
        
        if(actualPlayerState== PlayerState.RUN) {actualPlayerState= PlayerState.EAT;}//initialization purposes- transition from run
        
        //actualPlayerState= PlayerState.EAT;// if no invaders go to eat state unless power pill
        
        
        
        
        
            switch (actualPlayerState) {
            case ATTACK:
                System.out.println("run towards.");
                int minDistance = Integer.MAX_VALUE;
                Constants.GHOST minGhost = null;
                for (Constants.GHOST ghost : Constants.GHOST.values()) {
                    // If it is > 0 then it is visible so no more PO checks
                    if (game.getGhostEdibleTime(ghost) > 0) {
                        int distance = game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(ghost));
                        System.out.println("I See ghost"+ghost);
                        if (distance < minDistance) {
                            minDistance = distance; //go after closer edible ghost
                            minGhost = ghost;
                        }
                    }
                }

                if (minGhost != null) {
//                    System.out.println("Hunting Ghost");
                    return myMove =game.getNextMoveTowardsTarget(current, game.getGhostCurrentNodeIndex(minGhost), Constants.DM.PATH);
                }

                
                
                System.out.println("Cant see ghost,wonder random");
                moves = game.getPossibleMoves(current, game.getPacmanLastMoveMade());
                if (moves.length > 0) {
                	actualPlayerState = PlayerState.WONDER;
                    return moves[random.nextInt(moves.length)];   
                }
                break;
                case EAT:
                	if(current==91) {actualPlayerState = PlayerState.WONDER; break; /*return MOVE.UP;*/}//this is for fixing a bug
                	
                	//System.out.println("Cant see ghost,go to eat");
                    //System.out.println("eat the closer.");
                	if(doIseeEdibleGhost(game)) {actualPlayerState = PlayerState.ATTACK;}
                	
                    int[] pills = game.getPillIndices();
                    int[] powerPills = game.getPowerPillIndices();

                    ArrayList<Integer> targets = new ArrayList<Integer>();

                    for (int i = 0; i < pills.length; i++) {
                        //check which pills are available
                        Boolean pillStillAvailable = game.isPillStillAvailable(i);
                        if (pillStillAvailable != null) {
                            if (pillStillAvailable) {
                                targets.add(pills[i]);
                            }
                        }
                    }

                    for (int i = 0; i < powerPills.length; i++) {            //check with power pills are available
                        Boolean pillStillAvailable = game.isPillStillAvailable(i);
                        if (pillStillAvailable != null) {
                            if (pillStillAvailable) {
                                targets.add(powerPills[i]);
                            }
                        }
                    }

                    if (!targets.isEmpty()) {
                        int[] targetsArray = new int[targets.size()];        //convert from ArrayList to array

                        for (int i = 0; i < targetsArray.length; i++) {
                            targetsArray[i] = targets.get(i);
                        }
                        //return the next direction once the closest target has been identified
                        return myMove =game.getNextMoveTowardsTarget(current, game.getClosestNodeIndexFromNodeIndex(current, targetsArray, Constants.DM.PATH), Constants.DM.PATH);
                    }


                    
                    
                    //if we dont see pills GO TO WONDER STATE , edo mpenei sto telos se periptosi pou troei idi ke teliosoun
                    
                    moves = game.getPossibleMoves(current, game.getPacmanLastMoveMade());
                    if (moves.length > 0) {
                    	actualPlayerState= PlayerState.WONDER;
                        return moves[random.nextInt(moves.length)];
                    }
                    	break;
                                       	
                          
                case WONDER:
                	if(doIseePills(game)) {actualPlayerState = PlayerState.EAT;}
                	if(doIseeEdibleGhost(game)) {actualPlayerState = PlayerState.ATTACK;}
                	
                    moves = game.getPossibleMoves(current, game.getPacmanLastMoveMade());
                    if (moves.length > 0) {
                    	//actualPlayerState = PlayerState.WONDER;
                    	
                        return moves[random.nextInt(moves.length)];   
                    }
             
                default:
                    //System.out.println("");
                    break;
            
        }
        return myMove;
    }
}