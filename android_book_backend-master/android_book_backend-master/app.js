// app.js
const express = require('express');
const app = express();
var bodyParser = require('body-parser');
var {hash ,compare}= require('./hashing.js');
var {
    findbyemail,
    save,
    findbyid,
    userupdate,
    bookadd,
    addtags,
    increasepopularity,
    bookpost,
    mybook,
    deletebook,
    editbook,
    findbookbyid,
    createwishlist,
    additem,
    findwishlist,
    removeitem,
    allbook
}= require('./db.js');
var {findbook}= require('./search');
const port=process.env.PORT||3000;
app.use(bodyParser.urlencoded({ extended: true }));

// parse application/json
app.use(bodyParser.json());

/*
* inputs:{
*   email:"",
*   password:""
*   }
*
 * output : "failure" or  mongo id
* */
app.post("/register",function(req,res){
    console.log(req.body);
    var body= req.body;
    hash(body.password,function(err,hash){
        body.password= hash;
        save(body,function(err,data){
            if(err)
                return    res.send("failed");
            if(data){
                createwishlist(data._id);
                return  res.send(data._id);
            }
            else
                return res.send("failed");
        });
    });
});


/**
 *inputs:{
 * name:"",
 * author:"",
 * tags:""
 * }
 *
 * output
 * serverId:" null in case of any failure"
 *
 */

app.post("/addBook",function(req,res){
    var body =req.body;
    bookadd(body,function(err,data){
        if(err)
            return   res.send("failed");
        if(data)
            return  res.send(data._id);
        else
            return  res.send("failed");
    });
});


/*
* inputs :{
* name:"",
* author:"",
* tag:"",
* serverId:""
* }
*
* output:" "failure" or "success""
* */
app.post("/updateBook",function(req,res){
    var body={};
    if(req.body.name)
        body.name= req.body.name;
    if(req.body.author)
        body.author= req.body.author;
    if(req.body.tag)
        body.tag= req.body.tag;
    var bookid= req.body.serverId;
    editbook(bookid,body,function(err,data){
        if(err)
            res.send("failed");
        if(data)
            res.send('success');
        else
            res.send("failure");
    });
});


app.get("/getBooks",function (req, res) {
    allbook(function(err,data){
        res.send(data);
    });
});



/*
* input:{query:"book name or something like that"}
*
* output :"array of books which best match to query"
*
* */

app.post("/searchBooks",function (req, res) {
    var search = req.body.query;
    allbook(function(err,data){
        findbook(search,data,function(data1){
            res.send(data1);
        });
    });
});


/////////////////////////////////////// new routes////////////

/*
* inputs
 * { username:"",
 *   password:""
* }
*
* outputs
* {
*  status:" success or failed"
*  username:"",
*  phone:"",
*  id:"mongo id of user"
* }
*
* */

app.post("/login",function (req, res) {
    var email = req.body.email;
    var password = req.body.password;
    findbyemail(email,function(err,data){
        if(!data)
            res.send({status:"failed"});
        else{
            compare(password,data.password,function(err,check){
                if(check){
                    var user={};
                    user.name=data.name;
                    user.no=data.no;
                    user.email=data.email;
                    user.id= data._id;
                    user.status='success';
                    res.send(user);
                }
                else{
                    res.send({status:"failed"});
                }
            });
        }
    });
});




/*
* input  {id:"user mongoId"}
*
* output: {status:"success or failed"
*          name:"",
*          phoneNumber:""
* }
*
*
* viewProfile implement karne ke liye addBook function mein user ki id bhi save karni hogi
* books ke saath
* */
app.post("/viewProfile",function (req, res) {
    var id = req.body._id;
    findbyid(id,function(err,data){

        if(!data)
            res.send({status:"failed"});
        else{
            var str={};
            str.status= 'success';
            str.name= data.name;
            str.phoneNumber= data.no;
            res.send(str);
        }
    });
});




// further routes
/*
  inputs:{
   id:
  },
  output:{
  an array of books
  }
*/
app.post('/mybooks',function(req,res){
    var id = req.body._id.toString();
    mybook(id,function(err,data){
        res.send(data);
    });
});



/*
* input {userId:"user mongo id"}
*
* output: wish list books Array
* }
*
* */

app.post("/getMyWishList",function (req, res) {
    var userid= req.body.userId;
    findwishlist(userid,function(err,data){
        res.send(data);
    });
});


/*
* saves book in my wish list
*
* input :{id:""}//id of book
*type :"my book or in wish list"
* output:success or failed
* */

app.post("/addToWishList",function (req, res) {
    var userid= req.body.userId;
    var bookid= req.body.id;
    additem(userid,bookid,function(err,data){
        if(err)
            res.send('failed');
        else
            res.send('success');
    });
});
/*
inputs :{
    userid:,
    id:
},
output:{
    status
}
*/
app.post('/removeitem',function(req,res){
    var userid= req.body.userId;
    var bookid = req.body.id;
    removeitem(userid,bookid,function(err,data){
        if(err)
            res.send({status:'failed'});
        else
            res.send({status:'success'});
    });
});
/*
for deleting a book
  inputs:{
    id:
  },
  output:{
    status:"success or failure"
  }
*/
app.post('/deletebook',function(req,res){
    var id = req.body.id;
    deletebook(id,function(err,data){
        if(err)
            res.send({status:"failed"});
        else{
            if(data)
                res.send({status:"success"});
            else
                res.send({status:"failed"});
        }
    });
});



app.listen(port,function(){
    console.log(`server running on ${port}'`);
});
