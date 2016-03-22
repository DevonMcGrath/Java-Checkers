/* Name: ResourceLoader
 * Author: Devon McGrath
 * Date: 07/21/2015
 * Description: This class loads resources for the program.
 */


//Package for display and graphics
package tools;

//Imports
import java.awt.Image;
import javax.imageio.ImageIO;


public class ResourceLoader {

	//Method that loads the images for the program
	public static Image loadImage(String path){
		
		//Image
		Image img = null;
		
		//Get the image
		try{
			img = ImageIO.read(ResourceLoader.class.getResource(path));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		//Return the image
		return img;
	}
}
