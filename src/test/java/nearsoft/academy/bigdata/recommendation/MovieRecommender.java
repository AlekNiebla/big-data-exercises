/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nearsoft.academy.bigdata.recommendation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 *
 * @author alejandro
 */
public class MovieRecommender {
    
    public String movieTxtOrigin;
    int numReviews=0,numProducts=0, numUsers=0;
    private Hashtable<String, Integer> Products = new Hashtable<String, Integer>();
    private Hashtable<String, Integer> Users = new Hashtable<String, Integer>();
    
    public MovieRecommender(String path) throws IOException{
        this.movieTxtOrigin=path;
        BuildInformation();
        
    }
    
    public void BuildInformation() throws FileNotFoundException, IOException{
        
      try{
        String line; 
        String userId="",productId="",score="";
        int currentProduct=0, currentUser=0;
        FileReader f = new FileReader(movieTxtOrigin);
        BufferedReader b = new BufferedReader(f);
        
        File moviesCSV = new File("moviesOutput.csv");
        FileWriter fileWriter = new FileWriter(moviesCSV);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        
        while((line = b.readLine())!=null) {
          
            if((line.contains("product/productId"))==true){
                
                productId=line.substring(19,29);
                
                if (Products.containsKey(productId)){
                        currentProduct = Products.get(productId);
                    } else{
                        Products.put(productId,numProducts);
                        currentProduct = Products.get(productId);
                        numProducts ++;
                    }
                
            }
          
            if((line.contains("review/userId"))==true){
              
                userId=line.substring(15);
                
                if (Users.containsKey(userId)){
                        currentUser = Users.get(userId);
                    } else{
                        Users.put(userId,numUsers);
                        currentUser = Users.get(userId);
                        numUsers ++;
                    }
            }
            if((line.contains("review/score"))==true){
              
                score=line.substring(14,17);
                bufferedWriter.write(currentUser + "," + currentProduct + "," + score + "\n");
                numReviews ++;
            }
          
                  
          
        }
        bufferedWriter.close();
        b.close();
             
      
      }
      catch(IOException e){
          
      }
        
    }
    
    
    public List<String> getRecommendationsForUser(String user) throws IOException, TasteException{
        int userId = Users.get(user);
        List<String> recommendationsList = new ArrayList<String>();
        
        try{
         DataModel model = new FileDataModel(new File("moviesOutput.csv"));
         
         UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        
         UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);

         UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
        
         List<RecommendedItem> rec = recommender.recommend(userId, 3);
                    for (RecommendedItem recommendation : rec) {
                        
                        recommendationsList.add(getProductName((int)recommendation.getItemID()));
                    }
           return recommendationsList;         
           
        }
        catch(IOException te){
            
        }
        
        return null;
    }
   
   public String getProductName(int value){
        Enumeration e = Products.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            if (Products.get(key)==value) {
                return key;
            }
        }
        return null;
    }
    
    public int getTotalReviews(){

        return numReviews;
    }
    
    public int getTotalProducts(){
        return numProducts;
    }
    
    public int getTotalUsers(){
        return numUsers;
    }
    
    
}
