go into plan mode 
 I need a simplified API that supplies product details for use in an item comparison feature. The implemetation should follow back-end best practices(I    │
│   suggest using a layered mayde hexagonal architecture), providing clear and efficient endpoints to retrive the required data for product comparisons. The  │
│   requirements are: API endpoint: Build a basic RESTful API that returns details for multiple items to be compared. THe API should provide fields such as   │
│   product name, image URL, description, price, rating, and specifications. SHould include basic error handling and inlinecomments to explain the logic. I   │
│   suggest using webflux for performance. Do not use real data bases, persist everything in local JSON or CSV files, I suggest avoiding CSV only using       │
│   them if absoletly necesary. USe good practices, SOLID principles and design patterns, here by the looks of it we can use mostly behavioral or structural  │
│   patterns I dont see many creational patterns but you can use them if you see it fit, use good error handling and proper documentation. Give me a plan
Ok, we are going to focus on one thing at the time, we are going to revamp the comparision method, can you improve that comparision method? Give me ideas  │
│   in wich we can improve that 
 Ok, lets focus our efforts on ProductComparisonAnalyzerService I have my concerns. The first concern is that we are not using Mono or Flux in this stage  │
│   so we might be having blocking issues, and the other one is that I think that we can break down or apply a design pattern to simplify the functionality   │
│   of that class. Go into planning mode to evaluate that    
Ok, review the entire project and update the readme documentation for this project, explain design patterns used, and project structure, future changes   │
│   and improvements (POST, PUT methods) go deep in that documentation  
Put in future changes on the readme that domain-events would be stored    