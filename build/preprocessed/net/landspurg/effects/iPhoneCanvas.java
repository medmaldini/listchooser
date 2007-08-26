package net.landspurg.effects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 * A replacement for MIDP2 List object. Replace an existing "List" display with this one
 * to have "iTunes" like display....<br/>
 * 
 */
public class iPhoneCanvas extends GameCanvas{
    Vector imaList=new Vector();
    Vector labels=new Vector();
    String title=null;
    int nbToDisp=7;  // Number of images to display
    int nbSteps=6;  // Number of steps for the animation
    int maxSize=5;  // Number of steps for the size anim
    
    int idx=0;
    int move=0;
    int counter=0;
    int currentSel=0;
    int reqwidth=100;
    int sizer=0;
    int reqWidth=getWidth()/2;
    int curSize=0;

    int curBig=0;
    Timer t=null;
    /**
     * 
     * @param title 
     * @param listType 
     */
    public iPhoneCanvas(String title,int listType){
        super(false);
        this.title=title;
    }
    /**
     *  
     * @param title 
     * @param listType 
     * @param labels 
     * @param images 
     */
    public iPhoneCanvas(String title, int listType, String[] labels, Image[] images) {
        super(false);
        if(images!=null){
            for(int i=0;i<images.length;i++)imaList.addElement(images[i]);
        }
        if(labels!=null){
            for(int i=0;i<labels.length;i++)this.labels.addElement(labels[i]);
        }
        this.title=title;
        currentSel=images.length/2;
    }

    CommandListener listener=null;
    public void setCommandListener(CommandListener cl){
        listener=cl;
        super.setCommandListener(cl);
    }
    public int append(String stringPart,Image imagePart){
        labels.addElement(stringPart);
        if(imagePart!=null){
            imaList.addElement(imagePart);
        }
        return labels.size();
    }
    public void set(int elementnum,String stringpart,Image imagePart){
        labels.setElementAt(stringpart,elementnum);
        imaList.setElementAt(imagePart,elementnum);
    }
    public void delete(int elmentNum){
        labels.removeElementAt(elmentNum);
        imaList.removeElementAt(elmentNum);
    }
    public int getSelectedIndex(){
        return idx;
    }
    public void setSelectedIndex(int idx,boolean flag){
        if(flag==true)this.idx=idx;
    }
    public void deleteAll(){
        labels.removeAllElements();
        imaList.removeAllElements();
    }
    /**
     * 
     * @param key 
     */
    public void keyPressed(int key){
        int code=getGameAction(key);
        switch(code){
            case UP:
                sizer=1;
                curBig=getMod(idx+(nbToDisp-1)/2);
                break;
            case DOWN:
                sizer=-1;
                break;
            case LEFT:
                if(curSize!=0)sizer=-1;
                counter=nbSteps-1;
                idx++;
                move=-1;
                break;
            case RIGHT:
                if(curSize!=0)sizer=-1;
                if(counter!=0){
                   if(move==1)idx-=move;
                    counter=0;
                }
                move=1;
                break;
                
            case FIRE:
                if(listener!=null)listener.commandAction(List.SELECT_COMMAND,this);
                break;
        }
        int timer=100;
        if(t==null){
            t=new Timer();
            t.schedule(new TimerTask(){
                public void run(){
                    if(sizer!=0){
                        curSize+=sizer;
                        if((curSize<0)||(curSize>=maxSize)){
                           if(curSize<0)curSize=0;
                           if(curSize>=maxSize)curSize=maxSize;
                           sizer=0;
                        }
                    }
                    counter+=move;
                    if((counter>=nbSteps)||(counter<0)){
                        if(move==1)idx-=move;
                        counter=0;
                        move=0;
                    }
                    if((move==0)&&(sizer==0)){
                        t.cancel();
                        t=null;
                    }
                    repaint();
                };
            },timer,timer
                    );
            
        }
        repaint();
    }
    String getString(int idx){
        return (String)labels.elementAt(idx);
    }
    private int getMod(int idx){
        do{
            idx=(idx+10*imaList.size())%imaList.size();
        }while(idx<0);
        return idx;
    }
    Image   getImage(int idx){
        return (Image)imaList.elementAt(idx);
    }
    /**
     * 
     * @param g 
     */
    public void paint(Graphics g){
        try{
        Font f=Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_LARGE);
        g.setColor(0x0);
        g.fillRect(0,0,getWidth(),getHeight());
        int py=(getHeight())/2;
        if(py>50)py-=20;
        int startX=(getWidth()-((nbToDisp-1)/2)*100)/2;
        int dx=50*counter/nbSteps;
        int angle=counter*45/nbSteps;
        int bar[]=new int[getWidth()];
        for(int i=0;i<bar.length;i++){
            bar[i]=1000;
        }
        for(int i=0;i<nbToDisp;i++){
            int anglel=-angle-(i-(nbToDisp-1)/2)*45;
            if(anglel<-45)anglel=-45;
            if(anglel>45)anglel=45;
            int curWidth=reqWidth;
            if(getMod(idx+i)==curBig)curWidth=reqWidth+(getWidth()-reqWidth)*curSize/maxSize;
            paintAnIma(bar,g,getImage(getMod(idx+i)),curWidth,startX+i*50+dx,py,anglel);
        }
        //
        // Display the label if any
        //
        g.setFont(f);
        if(labels!=null){
            String label=getString(getMod(idx+(nbToDisp-1)/2));
            if(label!=null){
                int val=0xFF*(nbSteps-counter)/nbSteps;
                g.setColor((val<<16)+(val<<8)+val);
                g.drawString(label,getWidth()/2,getHeight(),Graphics.HCENTER|Graphics.BOTTOM);
            }
        }
        if(title!=null){
                g.setColor(0xFFFFFF);
                g.drawString(title,getWidth()/2,0,Graphics.HCENTER|Graphics.TOP);
            
        }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     *x: cnter of the image
     *y: center
     *angle: angle to rotate (-45 to 45)
     */
    
    public void paintAnIma(int bar[],Graphics g,Image ima,int wantedWidth,int x,int y,int angle) {
        int stepx=1;
        if(ima==null)return;
        int widthi=ima.getWidth();
        int heighti=ima.getHeight();
        int width=wantedWidth;
        int height=heighti*wantedWidth/widthi;
        
        int angleabs=angle;
        if(angleabs<0)angleabs=-angleabs;
        
        // ReqWidth: expedted width of the image
        // endHeigth: "ending" expected height
        // startHeight: "start height"
        int reqwidth=(90-angleabs)*width/90;
        int reqheight=height-(angleabs*height/4)/45;
        int startHeight=height;
        int endHeight=reqheight;
        
        
        if(angle<0){
            startHeight=reqheight;
            endHeight=height;
        }else{
        }
        x-=reqwidth/2;
/*        
        g.setColor(0xFF0000);
        
        g.drawLine(x,y-startHeight/2,x+reqwidth,y-endHeight/2);
        g.drawLine(x,y+startHeight/2,x+reqwidth,y+endHeight/2);
        g.drawLine(x,y-startHeight/2,x,y+startHeight/2);
        g.drawLine(x+reqwidth,y-endHeight/2,x+reqwidth,y+endHeight/2);
  */      
        //   g.drawImage(ima,x,y,Graphics.LEFT|Graphics.TOP);
        
        int src[]=new int[heighti];
        int buff[]=new int[height];
        int buffDest[]=new int[height];
//        System.out.println("angle:"+angle+" reqwidth:"+reqwidth+" startHeight:"+startHeight+" endHeight:"+endHeight);
        for(int j=0;j<reqwidth;j++){
            int curHeight=startHeight+(j*(endHeight-startHeight)/reqwidth);
            if( ((x+j)>0)&&((x+j)<getWidth())&&(bar[x+j]>y-curHeight/2)){
                
                bar[x+j]=y-curHeight/2;
                ima.getRGB(src,0,1,j*widthi/reqwidth,0,1,heighti);
                int max=curHeight/3;
/*
                for(int i=0;i<curHeight;i++){
                    int ypos=i*heighti/curHeight;
                    buff[i]=src[ypos];
                }
*/
                int dy=(heighti<<8)/curHeight;
                int ypos=-dy;
                for(int i=0;i<curHeight;i++){
                    buff[i]=src[(ypos+=dy)>>8];
                }
                
/*
                    for(int i=0;i<max;i++){
                    int alpha=0xA0*(max-i)/max;
                    alpha=alpha<<24;
                    buffDest[i]=(buff[curHeight-i-1]&0xFFFFFF)|alpha;
                    
                }
*/

//#ifdef OPTION1                
//#                long alpha=0xD0<<8;
//#                long dalpha=(alpha*70/100)/max;
//#                for(int i=0;i<max;i++){
//# //                    buffDest[i]=(buff[curHeight-i-1]&0xFFFFFF)|((int)((alpha&0xFF0000)<<8));
//#                    long val=buff[curHeight-i-1];
//#                    long r=(((val&0XFF0000)*alpha)>>16)&0xFF0000;
//#                    long v=(((val&0XFF00)*alpha)>>16)&0xFF00;
//#                    long b=(((val&0XFF)*alpha)>>16)&0xFF;
//#                  
//#                    buffDest[i]=(int)(r+v+b);
//# //                    buffDest[i]=(buff[curHeight-i-1]&0xFFFFFF)|((int)((alpha&0xFF0000)<<8));
//#                    alpha-=dalpha;
//#                     //System.out.println((alpha>>24)+" "+dalpha);
//#                }
//#endif
                
                long alpha=0xD0<<8;
                long dalpha=(alpha*70/100)/max;
                alpha=alpha<<8;
                for(int i=0;i<max;i++){
                    buffDest[i]=(buff[curHeight-i-1]&0xFFFFFF)|((int)((alpha&0xFF0000)<<8));
                    alpha-=dalpha;
                    //System.out.println((alpha>>24)+" "+dalpha);
                }
                g.drawRGB(buff    ,0,1,x+j,y-curHeight/2,1,curHeight,false);
                g.drawRGB(buffDest,0,1,x+j,y+curHeight/2+2,1,max,true);
            }
        }
    }
}
