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
    public enum PlayerState {EAT, RUN,ATTACK,WONDER};
    PlayerState actualPlayerState = PlayerState.EAT;
    private Random random = new Random();
   // private Constants.GHOST EvadingGhost;
 
    public boolean doIseePills(Game game) {
    	// function that returns true if a pill is visible
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
    
    
    
    
    
    
    public MOVE getMove(Game game, long timeDue) {
        //Place your game logic here to play the game as Ms Pac-Man
        // Should always be possible as we are PacMan
        int current = game.getPacmanCurrentNodeIndex();
        int invadingGhostLocation=0;
        if(game.wasPacManEaten()) {actualPlayerState= PlayerState.EAT;}//initial state
        
        System.out.println(actualPlayerState);
        MOVE[] moves = game.getPossibleMoves(current, game.getPacmanLastMoveMade());
        //check for invaders
        for (Constants.GHOST ghost : Constants.GHOST.values()) {
            // If can't see these will be -1 so all fine there
            if (game.getGhostEdibleTime(ghost) == 0 && game.getGhostLairTime(ghost) == 0) {
                int ghostLocation = game.getGhostCurrentNodeIndex(ghost);
                if (ghostLocation != -1) {
                    if (game.getShortestPathDistance(current, ghostLocation) < 20) {
                        //System.out.println("Evading Ghost"+ghostLocation+"pacman location"+current);
                    	///EvadingGhost=ghost;
                    	actualPlayerState= PlayerState.RUN;
                        return myMove =game.getNextMoveAwayFromTarget(current, ghostLocation, Constants.DM.PATH);
                    }
                }
            }
            if(game.wasPowerPillEaten()||(game.getGhostEdibleTime(ghost) > 0 && game.getGhostLairTime(ghost) == 0)) {
            actualPlayerState= PlayerState.ATTACK;
            System.out.println("Power Pill active");}
            
        }
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
                            minDistance = distance;
                            minGhost = ghost;
                        }
                    }
                }

                if (minGhost != null) {
//                    System.out.println("Hunting Ghost");
                    return myMove =game.getNextMoveTowardsTarget(current, game.getGhostCurrentNodeIndex(minGhost), Constants.DM.PATH);
                }
                //actualPlayerState= PlayerState.EAT;
                System.out.println("Cant see ghost,wonder random");
                moves = game.getPossibleMoves(current, game.getPacmanLastMoveMade());
                if (moves.length > 0) {
                	actualPlayerState = PlayerState.WONDER;
                    return moves[random.nextInt(moves.length)];   
                }
                //break;
                break;
                case EAT:
                	//System.out.println("Cant see ghost,go to eat");
                    //System.out.println("eat the closer.");
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
//                        System.out.println("Hunting pill");
                        return myMove =game.getNextMoveTowardsTarget(current, game.getClosestNodeIndexFromNodeIndex(current, targetsArray, Constants.DM.PATH), Constants.DM.PATH);
                    }
                    //System.out.println("going to pill random ???");

                    //if we dont see pills search the map
                    
                    	//int[] Activepills =game.getActivePillsIndices();
                    moves = game.getPossibleMoves(current, game.getPacmanLastMoveMade());
                    if (moves.length > 0) {
                    	actualPlayerState= PlayerState.WONDER;
                        return moves[random.nextInt(moves.length)];
                    }
                    	break;
                        
                case RUN:
                    //System.out.println("run away.");
                	//return myMove =game.getNextMoveAwayFromTarget(current, invadingGhostLocation, Constants.DM.PATH);
                	
                	actualPlayerState= PlayerState.EAT;
                    break;
                          
                case WONDER:
                	if(doIseePills(game)) {actualPlayerState = PlayerState.EAT;}
                	
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