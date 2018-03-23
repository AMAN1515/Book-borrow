var mongoose=require('mongoose');
var schema = mongoose.Schema;
mongoose.connect('mongodb://main:main@ds245548.mlab.com:45548/bstore');

var userschema = new schema ({
    name :{
        type:String
    },
    email:{
        type:String ,
        required:true,
        unique:true
    },
    password:{
        type:String,
        required:true
    },
    no:{
        type:Number
    }
});
var bookschema = new schema({
    userid:{
        type:String ,
        required:true
    },
    name :{
        type:String ,
        required:true
    },
    author:{
        type:String
    },
    tags:[String]
    ,
    popularity:{
        type:Number,
        default :0
    }
});
var cartschema = new schema ({
    userid:{
        type:String,
        unique:true
    },
    bookids:[String]
});
var user= mongoose.model('user',userschema);
var book= mongoose.model('book',bookschema);
var cart = mongoose.model('cart',cartschema);
var save=function(data,callback){
    var new_user= new user(data);
    new_user.save(callback);
};
var findbyemail=function(email,callback){
    user.findOne({email:email},callback);
};
var findbyid = function(id,callback){
    user.findById(id,callback);
};
var userupdate= function(id,data,callback){
    user.update({_id:id},{name:data.name,no:data.no},callback);
};
var bookadd = function(new_book,callback){
    var book1 = new book(new_book);
    book1.save(callback);
};
var addtags= function(bookid,tagname,callback){
    book.update({_id:bookid},{$push: {tags: tagname}},callback);
};
var increasepopularity= function(bookid,userid,callback){
    book.findOne({_id:bookid}).then(function(data){
        if(data.userid.toString()!=userid.toString())
            book.update({ _id:bookid},{$inc:{popularity:1}},callback);
        else
            callback(null,'your data cant be ');
    });
};
var  bookpost= function(userid,callback){
    book.find({userid:{$not:{$in : [userid]}}}).sort({popularity:-1}).then(callback);
};
var mybook = function(id,callback){
    book.find({userid:id},callback);
};
var editbook = function(bookid,data, callback){
    book.update({_id:bookid},data,callback);
};
var deletebook = function(bookid, callback){
    book.remove({_id:bookid},callback);
};
var findbookbyid = function(bookid,callback){
    book.findOne({_id:bookid},callback);
};
var allbook = function(callback){
    book.find({},callback);
};
var createwishlist = function(userid){
    var data={userid:userid};
    var new_cart = new cart(data);
    new_cart.save().then(function(data){
        console.log(data);
    });
};
var additem = function(userid,bookid,callback){
    cart.findOne({userid:userid, bookids: bookid},function(err,data){
        if(!data){
            cart.update({userid:userid },{$push: {bookids: bookid.toString()}},callback);
        }
        else{
            callback(err,data);
        }
    });
};
var findwishlist = function(userid, callback){
    cart.findOne({userid:userid}).then(function(data){
        book.find({_id :{ $in : data.bookids}},callback);
    });
};
var  removeitem = function(userid ,bookid, callback){
    userid=userid.toString();
    bookid= bookid.toString();
    cart.update({userid:userid},{$pull :{bookids : bookid}},callback);
};
module.exports={
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
};
//editbook ('5a62f365dbe55622849bf606',{name:'shvshsc'},function(err,data){
// console.log(data);
//})
//removeitem("pulkit","job",function(err,data){
//  console.log(data);
//});ss
var clear=function(){
    user.remove().then(function(data){
        console.log(data);
    });
    book.remove().then(function(data){
        console.log(data);
    });
    cart.remove().then(function(data){
        console.log(data);
    });
};