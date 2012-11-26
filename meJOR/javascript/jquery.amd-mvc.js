(function($,w,doc,regexp,nil,undef){
	var head=$('head')[0];
	$.extend({
		/**
	     *  创建一个新的构造器
	     *  @param args:{
	     *     $name:'',//组件名
	     *     $super:function(){},//超类构靠器
	     *     constructor:function(args){},
	     *     ......properties and methods
	     *  }
	     *  @return 返回值是一个对象的构造器C。 C.$super指向超类的prototype。可以使用C.$super.$super得到超类的超类的prototype
	                用这个构造器构造的新对象内部使用如下代码this.$super===C.$super，结果是true; C.prototype.$super===C.$super是true
	     */
	    declare:function(args){
	    	  function F(){}
	    	  return ($.declare=function(){
	    	  	var $super=args.$super,proto=$super.prototype,constructor=args.constructor,C=function(){
		    	  	$super&&$super.apply(this,arguments);
		    	  	constructor&&constructor.apply(this,arguments);
		    	  },fproto;
		    	  F.prototype=proto;
		    	  fproto=new F();
		    	  C.prototype=fproto;
		    	  C.$super=proto;
		    	  $.extend(fproto,args);
		    	  return C;
	    	  })(args);
	    },
	    asyncLoadJs:function(path,callback){
	    	var script=document.createElement('script');
	    	script.src=path;
	    	head.appendChild(script);
	    	callback&&callback();
	    	return $;
	    },
	    syncLoadJs:function(component,callback){
	    	var script=document.createElement(component);
	    	
	    	return $;
	    },
	    namespace:function(name){
	    	name=name.replace(/^(\$|j[qQ]uery)\./,'').split('.');
	    	for(var i=0,l=name.length,ns=$;i<l;i++){
	    		var n=name[i];
	    		(n=ns[n])?(ns=n):(ns=ns[n]={});
	    	}
	    	return ns;
	    },
	    require:function(required,callback){
	    	var d=doc,root=$.projectRoot;
	    	for(var i=0,l=required-1;i<=l;i++){
	    		var req=required[i];
	    		if(req.lastIndexOf('.css')+4===req.length){
	    			var link=d.createElement('link');
	    			link.rel='stylesheet';
	    			link.href=$.startsWith(req,'/')?req:[root,req].join('/');
	    			head.appendChild(link);
	    		}else if(/^((\$|j[qQ]uery)\.)?([a-z]\.)*_*\$*(([A-Z]+[a-z]*)+\$?)+_*$/.test(req)){
	    			req=req.replace(/^(\$|jQuery)\./,'jquery.');
	    			req=$.startsWith('jquery.')?req:('jquery.'+req);
	    			try{
	    				if(!(new Function('return '+req))()){
	    					load($,root,req,i,l,callback);
	    				}
	    			}catch(e){
	    				load($,root,req,i,l,callback);
	    			}
	    		}else{
	    			if($.endsWith(req,'.js')){
	    				try{
	    					if(!(new Function('return '+req.replace(new regexp(['^',root,'\/|.js$'].join(''),'g'),'').replace(/\//g,'.')))()){
	    						$.syncLoad(req,i<l?null:callback);
	    					}
	    				}catch(e){
	    					$.syncLoadJs(req,i<l?null:callback);
	    				}
	    			}else{
	    				$.syncLoadJs(req,i<l?null:callback);
	    			}
	    		}
	    		function load($,root,req,i,l,callback){
	    			req=[root,'/',req.replace(/\./g,'/'),'.js'].join('');
	    			$.syncLoad(req,i<l?null:callback);
	    		}
	    	}
	    },
	    define:function(required,namespace,def){
	    	switch(arguments.length){
	    	case 1:
	    		return define([],'',def);
	    	case 2:
	    		return define([],required,namespace);
	    	case 3:
    			if(typeof required==='string'){
	    			return define([required],namespace,def);
	    		}else{
	    			return $.require(required,function(){
		    			var name=def.$name,lastDot;
		    			if(!name){
		    				if((lastDot=namespace.lastIndexOf('.'))>0){
		    					name=namespace.substring(lastDot+1);
			    				namespace=namespace.substring(0,lastDot);
		    				}else{
		    					name=namespace;
		    					namespace='';
		    				}
		    			}
	    				namespace=$.namespace(namespace);
		    			def=$.declare(def);
		    			if(name){
		    				namespace[name]=def;
		    			}
		    			return def;
	    			});
	    		}
	    	}
	    }
	});
})(jQuery,window,document,RegExp,null,undefined);