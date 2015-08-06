function GetLineData(myJavaObject)  
   for i = 0, 2*math.pi, 0.01 do  
     myJavaObject:addValue(math.sin(i));  
   end  
 end