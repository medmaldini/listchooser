/*
 * iPhoneTest.java
 *
 * Created on 13 mars 2007, 20:09
 */

package net.landspurg.tests;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import net.landspurg.effects.*;

/**
 *
 * @author  thomas
 * @version
 */
public class iPhoneTest extends MIDlet implements CommandListener{
    Display m_disp;
    Displayable canvas,list;
    Command cmdSwitch=new Command("Switch",Command.SCREEN,1);
    Command cmdExit=new Command("Exit",Command.EXIT,2);
    
    public void init(Displayable theDisplay){
            theDisplay.addCommand(cmdSwitch);
            theDisplay.addCommand(cmdExit);
            theDisplay.setCommandListener(this);
    }
    public void startApp() {
        m_disp=Display.getDisplay(this);
        try{
            Image ima[]=new Image[6];
            ima[0]=Image.createImage("/noa.png");
            ima[1]=Image.createImage("/calog.png");
            ima[2]=Image.createImage("/popol.png");
            ima[3]=Image.createImage("/zazie.png");
            ima[4]=Image.createImage("/icon8.png");
            ima[5]=Image.createImage("/star.png");
            String labels[]={"Noa","Calogero","Polnareff","Zazie","a","b"};
            canvas=new iPhoneCanvas("Disques",List.IMPLICIT,labels,ima);
            list=new List("Disques",List.IMPLICIT,labels,ima);

            init(list);
            init(canvas);
            ((Canvas)canvas).setFullScreenMode(true);
            m_disp.setCurrent(canvas);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void pauseApp() {
    }
    
    public void destroyApp(boolean unconditional) {
    }

    public void commandAction(Command command, Displayable displayable) {
        if(command.getCommandType()==Command.EXIT){
            this.destroyApp(true);            
        }else if(command==List.SELECT_COMMAND){
            System.out.println("Select....");
        }else{
        
            if(m_disp.getCurrent()==canvas){
                m_disp.setCurrent(list);
            }else{
                m_disp.setCurrent(canvas);
            }
        }
    }
}
