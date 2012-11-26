(function($){
	jquery=$;
	$.fn.extend({
		/**
		 * 为方便判断一个对象是jquery对象
		 */
	    isJQuery:true,
	    /**
	     * 判断当前jquery对象的第一个元素是不是dom
	     * */
	    isDom:function(){
	    	return $.isDom(this[0]);
	    },
	    /**
	     * 把元素加载完成时触发onload/onreadystatechange事件
	     * 在触发onload元素时执行回调函数
	     * 或在触发onreadystatechang事件时且e.target.readyState是'loaded'或'complete'时执行回调函数
	     * @param fn dom加载完成时执行的回调函数
	     */
	    loadBind:function(fn){
	    	this.bind('readystatechange load',function(e){
	    		var tar=e.target,state=tar.readyState;
	            if(!state || state==='loaded' || state==='complete'){
	            	var evthdl=arguments.callee;
	                $(tar).unbind('readystatechange',evthdl).unbind('load',evthdl);
	                fn(e);
	            }
	         });
	         return this;
	    },
	    /**
	     * 把jquery对象内所有的input、select元素序列化成如下形式的对象
	     * {
	     *   name1:value1,
	     *   name2:value2,
	     *   ...
	     * }
	     * 对象的属性名就是input或select元素的name属性的值
	     * 对象的属性值就是input元素的value属性的值，或被选中的option
	     * sameNameAsString:true|false；true:相同name属性的input或select,被序列化成字符串，每个值被splitor分隔
	     *                              false:相同属性的input或select，被序列化成数组
	     *                              默认是false
	     * splitor：默认是','
	     */
	    serializeToObj:function(sameNameAsString,splitor){
	    	var o={};
	    	splitor=splitor||',';
	    	this.find(':input').each(function(i,d){
	    		var n=d.name,d=$(d),nv=d.val();
	    		if(n){
	    			if(!o[n]){
		    			o[n]=nv;
		    		}else{
		    			var v=o[n];
		    			if(sameNameAsString){
		    				o[n]=[v,nv].join(splitor);
		    			}else{
		    				if($.isArray(v)){
			    				v.push(nv);
			    			}else{
			    				o[n]=[v,nv];
			    			}
		    			}
		    		}
	    		}
	    	});
	    	return o;
	    },
	    /**
	     * 把当前jquery对象的:input元素所有带点号的表单域的名字都作为一个对象处理
	     * eg.有一个表单元素是<input name="a.b" value="1"/>
	     * 序列化之后是{a:{b:1}}
	     */
	    serializeToComplexObj:function(){
	    	var c=o={};
	    	this.find(':input').each(function(i,d){
	    		d=$(d);
	    		var n=d.attr('name'),v=d.val();
	    		d=d.split('.');
	    		for(var di=0,dl=d.length-1,dn;di<dl;di++){
	    			dn=d[di];
	    			c=c[dn]={};
	    		}
	    		c[c[dl]]=v;
	    		c=o;
	    	});
	    	return o;
	    },
	    /**
	     * 从来没用过
	     * 用来选中一个dom元素内的全部文本
	     */
	    textSelection:function(){this.each(function(i,d){var range = d.createTextRange();range.moveStart("character",0);range.select();});},
	    /**
	     * 有了新表单Form.js实现此函数就过时了
	     * 用于初始化上传文件的表单
	     * args:{
	     *    id:'',
	     *    append:true|false,
	     *    name:'',
	     *    url:'',
	     *    namespace:'',
	     *    action:'',
	     *    fileType:'',//以逗号分隔的文件扩展名列表
	     *    errFileMsg:'',//指定了文件类型必须指定此值
	     *    successMsg:'',
	     *    errorMsg:'',
	     *    uploadOnSelected:true|false,//选定文件时自动提交
	     *    beforeUpload:function(file){},//参数是file域的值
	     *    showProgress:true|false,
	     *    uploading:function(){},//上传中
	     *    success:function(){},
	     *    error:function(){},
	     *    label:'',
	     *    style:'anchor'|'button'|'normal',//呈现样式，如果是前两种选择必须指定label，且file域会被隐藏，label域作为button或anchor的文本
	     *          //css.label是anchor|button的样式或style:normal时的label样式
	     *    uploadText:'',//此值用于初始化提交按钮，uploadOnSelected:true时不需要提交安钮，此值无效，
	     *    cls:{'selector':{}},
	     *    css:{'selector':{}},
	     *    fn:function(){}
	     * }
	     */
	    initFileupload:function(args){
	    	var callee=arguments.callee;
	    	var id=args.id||$.genSerialId('fileupload'),style=args.style||'normal',lbl=[],label=args.label,fileFieldName=args.name,
	    	fieldid=id+'_file',file=['<input id="',fieldid,'" type="file" name="',fileFieldName,'"#HIDE#/>'].join(''),autoup=args.uploadOnSelected;
	    	if(label){
	    		lbl=['<label for="',fieldid,'">'];
	    		switch(style){
	    		case 'normal':
	    			lbl.push(label);
	    			file=file.replace('#HIDE#','');
	    			break;
	    		case 'anchor':
	    			lbl.push('<a href="javascript:void(0);">');
	    			lbl.push(label);
	    			lbl.push('</a>');
	    			break;
	    		case 'button':
	    			lbl.push('<button type="button">');
	    			lbl.push(label);
	    			lbl.push('</button>');
	    			break;
	    		}
	    		lbl.push('</label>');
	    		file=file.replace('#HIDE#',' style="display:none;"');
	    	}
	    	var upbtn=[];
	    	if(!autoup){
	    		upbtn=['<button type="submit">',(args.uploadText||'确定'),'</button>'];
	    	}
	    	var iframeid=id+'_iframe';
	    	var upload=['<form id="',id,'" target="',iframeid,'" method="post" enctype="multipart/form-data" action="',
	    	    (args.url||$.concatUrl(args.namespace,args.action))+'">',lbl.join(''),file,upbtn.join(''),
	    	    '<iframe id="',iframeid,'" name="',iframeid,'" style="display:none;"></iframe></form><div id="',id,
	    	    '_info" style="display:none;width:500px;height:300px;overflow:scroll;"></div>'].join('');
	    	if(args.append){
	    		this.append(upload);
	    	}else{
	    		this.html(upload);
	    	}
	    	var form=$('#'+id),beforeUp=args.beforeUpload||$.noop;
	    	if(!autoup){
	    		form.find(':submit').click(function(){
	    			if(false===beforeUp(form.find(':file').val())){
	    				return false;
	    			}else{
	    				(args.uploading||$.noop)();
	    				return true;
	    			}
	    		});
	    	}
	    	form.find('a,:button[type="button"]').click(function(){
	    		form.find(':file').click();
	    	});
	    	form.find(':button').button();
	    	if(style!='normal' && label){
	    		form.find(':file').hide();
	    	}
	    	var btnhtml='', btn=form.find('a,:button'),msgcss={top:255,left:325};
	    	form.find(':file').change(function(e){
	    		if($.validation.upload.fn(form,fileFieldName,args.fileType)){
	    			if(autoup){
	    				var valid=beforeUp(val);
	    				if(valid===undefined || valid===true){
	    					(args.uploading||$.noop)();
	    					form.submit();
	    				}
	    			}
	    		}else{
	    			$.flashMsg({msg:args.errFileMsg,type:'alert',css:msgcss});
	    		}
	    	});
	    	form.submit(function(){
	    		if(!$.validation.upload.fn(form,fileFieldName,args.fileType)){
	    			$.flashMsg({msg:args.errFileMsg,type:'alert',css:msgcss});
	    			return false;
	    		}else{
	    			if(btn.length>0){
	    				btnhtml=btn.html();
	    				btn.append(['<img id="',id,'_progress" src="themes/jquery/loading.gif"/>'].join(''));
	    			}
	    		}
	    	});
	    	var cls=args.cls||{};
	    	for(var n in cls){
	    		form.find(n).addClass(cls[n]);
	    	}
	    	form.addClass(cls.form||{});
	    	var css=args.css||{};
	    	for(var n in css){
	    		form.find(n).css(css[n]);
	    	}
	    	form.css(css.form||{});
	    	(args.fn||$.noop)();
	    	return id+'_info';
	    },
	    /**
	     * 与serializeToObj功能重复，应删除
	     * @returns uqf 此jquery对象表示的form序列化为json对象，并返回此json对象
	     */
	    serializeFormToObj:function(){
	    	var obj={};
	    	this.find(':input').each(function(i,input){
	    		obj[input.name]=$(input).val();
	    	});
	    	return obj;
	    },
	    /**
	     * 返回此jquery对象表示的第一个dom对象的id，如果没有就自动创建一个
	     * prefix:作为id的前缀，如果没有就以nodeName作为prefix
	     */
	    getDomId:function(prefix){
	    	var dom=this[0];
	    	var id=dom.id;
	    	if(!id){
	    		dom.id=id=$.genSerialId(prefix||dom.nodeName);
	    	}
	    	return id;
	    }
	});
	var toString=Object.prototype.toString;
	$.extend({
	    projectRoot:'/project',//前面的斜线不能丢,不能以/结尾
	    actionExtension:'do',
	    isJQueryRoot:true,//方便判断一个对象是jquery的根对象，也就是$
	    loggedUserHeader:'USER-ID-HEADER',//用于在cookie中记录登录的用户id，此值作为cookie的名
	    loggedUsernameKey:'LOGGED-USERNAME',//用于在cookie记录登录用户名，此值作为cookie的名
	    extra:{},
	    count:0,
	    /**
	     * 根据arg创建html文本，返回值是根据参数创建的文本
	     * args可以是数组或json对象
	     * 如果是数组，就认为每个数组元素是一个如下所述的json对象
	     * 如果是json对象，应遵守如下约定
	     * {
	     *     node:'',//节点的nodeName
	     *     attrs:{//属性，dom的各个属性都在此定义，整个attr对象构造成'attrName1="attrValue1" name2="value2"'的形式
	     *     	  attrName:NUMBER|''|boolean|['','',...]//attrName是dom元素的属性名，值是数字、字符串或数组,
	     *                                              //如果是数组就把数组转化成以空格分隔的字符串
	     *     },
	     *     styles:{
	     *       styleName:'styleValue'
	     *     },
	     *     children:[]|{}//子节点
	     * }
	     */
	    generateHtml:function(args){
	    	if($.isArray(args)){
	    		var html=[],htmlLen=0;
	    		for(var i=0,l=args.length;i<l;i++){
	    			html[htmlLen++]=$.generateHtml(args[i]);
	    		}
	    		return html.join('');
	    	}
	    	var node=args.node;
	    	var html=['<',node];
	    	var attrs=args.attrs;
	    	var htmlLen=html.length;
	    	if(attrs){
	    		html[htmlLen++]=' ';
	    		var id=attrs.id;
	    		if(!id || /^[a-zA-Z]+-\d+$/.test(id)){
	    			attrs.id=$.genSerialId(node);
	    		}
	    		for(var n in attrs){
	        		var attr=attrs[n];
	        		html[htmlLen++]=[n,'="',$.isArray(attr)?attr.join(' '):attr,'" '].join('');
	        	}
	    	}
	    	var styles=args.styles;
	    	if(styles){
	    		html[htmlLen++]=' style="';
	        	for(var n in styles){
	        		var style=styles[n];
	        		html[htmlLen++]=[n,':',style,';'].join('');
	        	}
	        	html[htmlLen++]='"';
	    	}
	    	var children=args.children;
	    	if('br input link meta'.indexOf(node)<0){
	    		html[htmlLen++]=['>',children?$.generateHtml(children):'','</',node,'>'].join('');
	    	}else{
	    		html[htmlLen++]='/>';
	    	}
	    	return html.join('');
	    },
	    /**
	     * 判断arg是不是dom
	     */
	    isDom:function(arg){
	    	return /[object HTML[A-Z][a-z]+Element]/.test(toString.call(arg));
	    },
	    /**
	     * 判断arg是不是正则表达式
	     * @param arg
	     * @returns {Boolean}
	     */
	    isRegExp:function(arg){
	    	return '[object RegExp]'===toString.call(arg);
	    },
	    /**
	     * 判断arg是不是Boolean
	     * @param arg
	     * @returns {Boolean}
	     */
	    isBoolean:function(arg){
	    	return typeof(arg)==='boolean';
	    },
	    /**
	     * 判断arg是不是字符串
	     */
	    isString:function(arg){
	    	return typeof(arg)==='string';
	    },
	    /**
	     * 如果src是以下值之一就返回true
	     * undefined null '' [] {}
	     */
	    isEmpty:function(src){
	       if(src===undefined || src===null || src==='' || (src instanceof Array && src.length===0)){
	           return true;
	       }else{
	           if(typeof(src)!=='string' && !src instanceof Array){
	               for(var n in src){
	                   if(src.hasOwnProperty(n)){
	                       return false;
	                   }
	               }
	               return true;
	           }
	           return false;
	       }
	    },
	    /**判断传入的参数是不是数字，参数是一个键盘键的charCode或keyCode。
	     * 返回true:是数字键；false:不是数字键。
	     * 另外退格 回车 delete tab 方向键返回true
	     *     shift+数字键返回false
	     * 如果要支持小数请把数字传入val参数
	     * copySupport:是否支持ctrl+c ctrl+v ctrl+x,默认是false,取值true时此方法在按下上述三个组合键时返回true
	     */
	    isNumberKey:function(which,shiftKey,ctrlKey,val,copySupport){
	    	return (copySupport && (ctrlKey && (which===67 || which===88 || which===86))) ||
		 	       (which===190 && (!val || val.indexOf('.')<0)) ||
		 	       ((!shiftKey && which>=48 && which<=57) || which===8 || which===46 || which===13 || which===9 || (which>=37 && which<=40)); 
	    },
	    /**
	     * 把参数的首字母改成大写再返回
	     * */
	    toFirstLetterUpperCase:function(s){return s.replace(/^[a-z]/,function(letter,idx){return letter.toUpperCase();});},
	    /**
	     * 把参数的首字母改成小写再返回
	     */
	    toFirstLetterLowerCase:function(s){return s.replace(/^[A-Z]/,function(letter,idx){return letter.toLowerCase();});},
	    /**
	     * 采用第一种参数是默认使用$.actionExtension作为扩展名
	     * 第一种参数
	     * namespace:url名称空间，与struts2 namespace概念一致
	     * actionName:与struts2 action name概念一致
	     * cache: boolean, 是否缓存,默认false
	     * 第二种参数{
	     * namespace:''
	     * actionName:''
	     * postfix:''
	     * cache:true|false
	     * param:''|{}
	     * }
	     */
	    concatUrl:function(namespace,actionName,param,cache){
	       if(namespace && namespace.constructor===Object){
	           var param=namespace.param||'',ns=namespace.namespace,act=namespace.actionName,ext=namespace.postfix,cache=namespace.cache;
	           if(ns==='/'){ns='';}
	           var url=[$.projectRoot,ns,'/',act,'.',ext||$.actionExtension].join('');
	           if(cache){url+=';'+new Date().getTime();}
	           if(param){
	               if(typeof(param)!=='string'){
	                   param=$.param(param,true);
	               }
	               url+='?'+param;
	           }
	           return url;
	       }
	       if(actionName){
	           var paramType=typeof(param);if(paramType==='boolean'){cache=param;param='';}if(namespace==='/'){namespace='';}
	           var url=[$.projectRoot,namespace,'/',actionName,'.',$.actionExtension].join('');
	           if(cache){url+=';'+new Date().getTime();}
	           if(param){var paramType=typeof(param);if(paramType!=='string'){param=$.param(param,true);}if(param){url+='?'+param;}}return url;
	       }else{return '';}
	    },
	    /**
	     *用splitor分割str,每个分割出的字符串作为参数调用fn
	     * splitor只接受字符串，不接受正则表达式
	     */
	    split:function(str,splitor,fn){var l=str.length,start=0,end=str.indexOf(splitor),spl=splitor.length;if(end<0){return fn(str);}else if(end===0){start=spl;end=str.indexOf(splitor,start);}if(end<0){end=l;return fn(str.substring(start,end));}for(;end<l-spl;){fn(str.substring(start,end));start=end+splitor.length;end=str.indexOf(splitor,start);if(end<0){end=l;}}},
	    /**
	     * date:String|Date|Number,//如果是Number或Date，按照format把date转化成string；如果是string，按照format把date转化成Date
	     *                         //如果date不是以上三种类型或者是null或者<=0或者是空串或者是undefined，函数会抛出异常
	     *                         //如果date是string，且不符合指定format，会抛出'IllegalDateString'
	     * formate:string//日期格式
	     * yyyy：四位数字的年份，yy：二位数字的年份
	     * MM：二位月份，M：一位月份
	     * dd：二位日期，d：一位日期
	     * HH：24小时制的二位小时，hh:12小时制的二位小时，H：24小时制的一位小时，h：12小时制的一位小时
	     * mm：二位分钟，m：一位分钟
	     * ss：二位秒，s：一位秒
	     * SSS：三位毫秒，S：一位毫秒
	     * ap: am/pm
	     * AP:AM/PM
	     */
	    dateFormat:function(format,date){
	        var cons=(date || date===0) && date.constructor,S=String,D=Date,N=Number;
	        if(cons===N){date=new D(date);cons=D;}
	        else if(cons!==D && cons!==S){throw 'illegal date param, date only accepts Date String and Number which is larger than zero';}
	        if(cons===D){
	            var preZero=function(n,ms){if(ms){if(n<10){return '00'+n;}else if(n<100){return '0'+n;}else{return n+'';}}else{if(n<10){return '0'+n;}else{return n+'';}}};
	            var hr12=function(h){if(h<=12){return h;}else{return h%12;}};
	            var hr=date.getHours(),hr12=hr12(hr)+'';
	            return format.replace(/yyyy/g,date.getFullYear()+'').replace(/yy/g,(date.getFullYear()%100)+'')
	                         .replace(/SSS/g,preZero(date.getMilliseconds(),true)).replace(/S/g,date.getMilliseconds()+'')
	                         .replace(/MM/g,preZero(date.getMonth()+1)).replace(/M/g,(date.getMonth()+1)+'')
	                         .replace(/dd/g,preZero(date.getDate())).replace(/d/g,date.getDate()+'')
	                         .replace(/HH/g,preZero(hr)).replace(/H/g,hr+'')
	                         .replace(/hh/g,preZero(hr12)).replace(/h/g,hr12)
	                         .replace(/mm/g,preZero(date.getMinutes())).replace(/m/g,date.getMinutes()+'')
	                         .replace(/ss/g,preZero(date.getSeconds())).replace(/s/g,date.getSeconds()+'')
	                         .replace(/ap/g,hr<12?'am':'pm').replace(/AP/g,hr<12?'AM':'PM');
	        }else if(cons===S){
//	            if(date.replace(/\d/g,'0')===format.replace(/[yMmdHhsS]/g,'0')){
	                var d=new D();var token='yyyy';var idx=format.indexOf(token);var tokend=2;
	                if(idx<0){token='yy';idx=format.indexOf(token);}
	                if(idx>=0){var y=N(date.substring(idx,idx+token.length));if(y<100){if(y>=70){y+=1900;}else{y+=2000;}}d.setFullYear(y);}
	                else{d.setFullYear(1);}
	                token='MM';idx=format.indexOf(token);if(idx<0){token='M';idx=format.indexOf(token);if(idx==format.indexOf('MS')){idx=-1;}}
	                if(idx>=0){if(token.length===2 || /\d\d/.test(date.substr(idx,2))){tokend=2;}else{tokend=1;}d.setMonth(N(date.substr(idx,tokend))-1);}
	                else{d.setMonth(0);}
	                token='dd';idx=format.indexOf(token);if(idx<0){token='d';idx=format.indexOf(token);}
	                if(idx>=0){if(token.length===2 || /\d\d/.test(date.substr(idx,2))){tokend=2;}else{tokend=1;}d.setDate(N(date.substr(idx,tokend)));}
	                else{d.setDate(1);}
	                token='HH';idx=format.indexOf(token);if(idx<0){token='H';idx=format.indexOf(token);}
	                if(idx>=0){if(token.length===2 || /\d\d/.test(date.substr(idx,2))){tokend=2;}else{tokend=1;}d.setHours(N(date.substr(idx,tokend)));
	                }else{token='hh';idx=format.indexOf(token);
	                	if(idx<0){token='h';idx=format.indexOf(token);}
	                	if(idx>=0){if(token.length===2 || /\d\d/.test(date.substr(idx,2))){tokend=2;}else{tokend=1;}
	                		var h=N(date.substr(idx,tokend)),ap=format.indexOf('ap');
	                		if(ap>=0 && format.substr(ap,2)==='pm'){h+=12;}
	                		d.setHours(h);}else{d.setHours(0);}}
	                token='mm';idx=format.indexOf(token);if(idx<0){token='m';idx=format.indexOf(token);}
	                if(idx>=0){if(token.length===2 || /\d\d/.test(date.substr(idx,2))){tokend=2;}else{tokend=1;}d.setMinutes(N(date.substr(idx,tokend)));}
	                else{d.setMinutes(0);}
	                token='ss';idx=format.indexOf(token);if(idx<0){token='s';idx=format.indexOf(token);}
	                if(idx>=0){if(token.length===2 || /\d\d/.test(date.substr(idx,2))){tokend=2;}else{tokend=1;}d.setSeconds(N(date.substr(idx,tokend)));}
	                else{d.setSeconds(0);}
	                token='SSS';idx=format.indexOf(token);if(idx<0){token='S';idx=format.indexOf(token);}
	                if(idx>=0){
	                	if(token==='SSS' || /\d\d\d/.test(date.substr(idx,3))){tokend=3;}
	                	else if(/\d\d/.test(date.substr(idx,2))){tokend=2;}
	                	else{tokend=1;}
	                	d.setMilliseconds(N(date.substr(idx,tokend)));
	                }else{d.setMilliseconds(0);}
	                return d;
//	            }else{throw 'IllegalDateString';}
	        }
	    },
	    /**
	     * table可以是tr选择器|jquery对象|tr的NodeList|tr数组
	     * 奇数行添加odd类名，偶数行添加even类名，鼠标移入时添加hover类名，移出时删除hover，类名对应的样式自己定义
	     * */
	    initRowColor:function(trs){trs=$(trs);trs.filter(':odd').addClass('odd');trs.filter(':even').addClass('even');trs.mouseover(function(){$(this).addClass('hover');}).mouseout(function(){$(this).removeClass('hover');});return trs;},
	    /**
	     * 仅在内部使用
	     * prefix:id前缀
	     */
	    genSerialId:function(prefix){
	    	var c=$.count,id=[prefix,$.now(),c].join('-');
	    	$.count=c+1;
	        return id;
	    },
	    /**
	     * 判断浏览器显示区域的高度，分别是screen.availHeight screen.height document.height body.height几个值中最小的
	     * @returns 一个数字（Number），单位是像素
	     */
	    minHeight:function(){
	    	var doc=document,body=doc.body,scr=screen;
			return Math.min(body.clientHeight,scr.availHeight,scr.height,$(doc).height(),$(body).height());
	    },
	    /**
	     * 判断浏览器显示区域的高度，分别是screen.availWidth screen.width document.width body.width几个值中最小的
	     * @returns 一个数字（Number），单位是像素
	     */
	    minWidth:function(){
	    	
	    	var doc=document,body=doc.body,scr=screen;
	    	return Math.min(body.clientWidth, scr.availWidth, scr.width,$(doc).width(),$(body).width());
	    },
	    /**
	     * 下面的四个方法用于保存和获取登录用户的id和用户名
	     * */
	    loggedUsername:function(){var name=arguments.callee.username;if(!name){arguments.callee.username=name=$.cookie($.loggedUsernameKey);}return name;},
	    saveLoggedUsername:function(username){$.loggedUsername.username=username;$.cookie($.loggedUsernameKey,username);},
	    loggedUserid:function(){var uid=arguments.callee.userid;if(!uid){uid=$.loggedUserid.userid=$.cookie($.loggedUserHeader);}return uid;},
	    saveLoggedUserid:function(userid){$.loggedUserid.userid=userid;var header=$.loggedUserHeader;$.cookie(header,userid+'');$.ajaxSetup({beforeSend:function(xhr){xhr.setRequestHeader(header,$.cookie($.loggedUserHeader));}});},
	    /**
	     * 现在有了新的Form.js，此方法应该过时了
	     * 创建一个隐藏的iframe，用于从服务端持续获取数据或不刷新下载
	     * args:{
	     *     id:'',//iframe的id
	     *     url:'',//iframe.src
	     *     namespace:'',//如果没有定义url，就试图用namespace和action构造一个url
	     *     action:'',//actionName
	     *     postfix:'',//url扩展名
	     *     param:''|{}//请求参数
	     * }
	     */
	    initIframe:function(args){
	        var id=args.id,param=args.param;
	        var url=args.url||$.concatUrl({namespace:args.namespace,actionName:args.action,postfix:args.postfix});
	        if(param){
	            if(typeof(param)!=='string'){
	                param=$.param(param,true);
	            }
	            url=[url,'?_=',new Date().getTime(),'&',param].join('');
	        }
	        var iframe=$('#'+id);if(iframe.length>0){iframe.remove();};
	        $(['<iframe id="',id,'" style="display:none;" src="',url,'"/>'].join('')).appendTo(document.body);
	    },
	    /**
	     * 此方法应该过时了
	     * 闪现消息
	     * args:{
	     *     id:'', //用于包装消息的tag id，可省略
	     *     css:{},//用于样式化包装消息的tag
	     *     msg:'',//将要闪现的消息，
	     *     type:'alert'|'info'|'error'//alert:警告信息，橘色背景，白色字；info：一般提示信息绿色背景，白色字；error:红色背景，白色字
	     *     speed:'fast'|'normal'|'slow'|NUMBER//可以是fast normal slow三个字符串之一，或者一个毫秒值；可省略默认1500ms
	     *     fadeInSpeed:'fast'|'normal'|'slow'|NUMBER//可以是fast normal slow三个字符串之一，或者一个毫秒值；可省略默认1500ms
	     *     fadeOutSpped:'fast'|'normal'|'slow'|NUMBER//可以是fast normal slow三个字符串之一，或者一个毫秒值；可省略默认1500ms
	     *     fadeOutDelay:NUMBER,
	     *     fn:function(){}
	     * }
	     * */
	    flashMsg:function(args){
	        var type=args.type;
	        if(typeof(args)==='string'){args={msg:args};}
	        var ms=args.spped||'1500',id=args.id||'__msgWrapper__',msgWrapper=$('#'+id);
	        if(msgWrapper.length==0){msgWrapper=$('<div style="padding:5 15 5 15;color:#ffffff;background-color:#00ff00;display:none;position:absolute;text-align:center;font:12px bold;z-index:999999;" id="'+id+'"></div>');$('body').prepend(msgWrapper);}
	        var bgcolor='#00ff00';if(type==='alert'){bgcolor='#ffaf0b';}else if(type==='error'){bgcolor='red';}
	        msgWrapper.html(args.msg);
	        var msgWidth=msgWrapper.width(),left=(screen.width-msgWidth)/2,css=args.css||{};
	        if(!css.left){css.left=left;}css['z-index']=9999999;css['background-color']=bgcolor;
	        msgWrapper.css(css).fadeIn(args.fadeInSpeed||ms);
	        setTimeout(function(){msgWrapper.fadeOut(args.fadeOutSpeed||ms,args.fn||$.noop);},args.fadeOutDelay||1500);
	    },
	    /**
	     * 此方法会自动为脚本url添加前缀
	     * 动态加载toLoad指定的css/js文件，从服务端动态生成的js也可以加载，要加载的css必须以css为后缀
	     * 所有toLoad都加载完成后执行callback
	     * toLoad:function|[String]|String,//如果是function必须返回字符串，每个字符串必须是合法的url
	     * callback：所有css/js加载完成后执行callback
	     * */
	    run:function(toLoad,callback){
	        var innerFn=function(toLoad,callback){
	            var loadFn=function(toLoad,doc){
	                var jsregex=/\.js(\?(\w+=.*)(&\2?)*)?/i,ld=loaded,urlBase=$.projectRoot;
	                if(!(toLoad && toLoad.length)){(callback||$.noop)();return;}
	                var url=toLoad.shift(),finalUrl=url,fullUrl='',absolutePath=url.indexOf('/')===0;
	                if(absolutePath){finalUrl=fullUrl=urlBase+url;}
	                var load=((url in ld) || (fullUrl && fullUrl in ld));
	                if(!load){
	                    if($.endsWith(url,'.css')){
	                        var link=doc.createElement('link');
	                        link.type='text/css';
	                        link.media='screen';
	                        link.rel='stylesheet';
	                        link.rev='stylesheet';
	                        link.href=finalUrl;
	                        loadFn.head.appendChild(link);
	                        ld[url]=ld[fullUrl]=true;
	                        loadFn(toLoad,doc);
	                    }else{
	                        if(jsregex.test(url)){
	                            var script=$('<script></script>').loadBind(function(e){var tar=e.target;ld[$(tar).attr('src')]=true;loadFn(toLoad,doc);}).get(0);
	                            ld[fullUrl]=ld[url]=script;
	                            script.src=finalUrl;
	                            loadFn.head.appendChild(script);
	                        }
	                    }
	                }else if(jsregex.test(url) && load && ld[url]!==true && ld[fullUrl]!==true){
	                    $(ld[url]).loadBind(function(e){loadFn(toLoad,doc);});
	                }else{
	                    loadFn(toLoad,doc);
	                }
	            };
	            if($.isFunction(toLoad)){toLoad=toLoad();}          
	            if(typeof(toLoad)==='string'){toLoad=[toLoad];}
	            else if(!$.isArray(toLoad)){callback();return;}
	            loadFn.head=$('head').get(0);loadFn($.uniq(toLoad),document);
	        };
	        var loaded={};
	        $('script').each(function(){loaded[$(this).attr('src')]=true;});
	        $('link').each(function(){loaded[$(this).attr('href')]=true;});
	        $('style').each(function(){
	            var style=$(this).html();
	            for(var importIdx=ref.indexOf('@import'), 
	                    start=ref.indexOf('"',importIdx+'@import'.length)+1,
	                    end=ref.indexOf('"',start);
	                start>0 && end>0;
	                importIdx=ref.indexOf('@import',end+1),
	                start=ref.indexOf('"',importIdx+'@import'.length)+1,
	                end=ref.indexOf('"',start)){
	                loaded[ref.substring(start,end)]=true;
	            }
	        });
	        innerFn(toLoad,callback);
	        this.run=innerFn;
	    },
	    /**
	     * 为数组去重并返回去重后的新数组，原数组不变
	     * arr:要去重的数组
	     * deep:是否深度搜索，默认是false
	     */
	    uniq:function(arr,deep,isdoms){if(isdoms){return $.unique(arr);}var exists={},result=[];for(var i=0,l=arr.length;i<l;i++){var cur=arr[i];if(deep && $.isArray(cur)){cur=arguments.callee(cur,true);}if(!exists[cur]){exists[cur]=true;result[result.length]=cur;}}return result;},
	    /**
	     * 判断字符串str是否以sub开头
	     */
	    startsWith:function(str,sub){return str.indexOf(sub)===0;},
	    /**
	     * 判断字符串str是否以sub结束
	     */
	    endsWith:function(str,sub){
	    	var idx=str.lastIndexOf(sub);
	    	return idx>=0 && idx+sub.length===str.length;
	    },
	    /**
	     * 判断src是否包含dst，通常用于判断一个字符串是否包含另一个，当然也可以判断对象属性或数组元素
	     * 仅用于判断字符串、数组、js对象
	     */
	    containsObject:function(src,dst){
	    	if($.isString(src)){
	    		return src.indexOf(dst)>=0;
	    	}else if($.isArray(src)){
	    		for(var i=0,l=src.length;i<l;i++){
	    			if(src[i]===dst){
	    				return true;
	    			}
	    		}
	    		return false;
	    	}else{
	    		for(var n in src){
	    			if(src[n]===dst){
	    				return true;
	    			}
	    		}
	    		return false;
	    	}
	    },/**
	     * 把obj转化成json串
	     * 此方法具有以下行为
	     * 把正则表达式对象去掉开头结尾的/,并转化成json格式的字符串
	     * 把函数转化成json格式的字符串
	     * 如果数组元素如果是undefined会被转化成"null", 如果对象的属性值是undefined会被忽略
	     * 任意NaN值且typeof结果是'number'的会被转化成"null",Infinity会被转化成"null"
	     * Date默认转化成number，如果传入dateFormat就使用dateFormat作为转化器，把它的返回值作为Date转化结果
	     * dateFormat是一个表示日期格式的字符串，serializeJSON调用$.dateFormat(format,date)把相应的日期属性按照dateFormat指定的日期格式转化成字符串
	     * ignoreFunction:true|false---true：把function类型转化成空字符串，false:把function转化成字符串
	     */
	    serializeJSON:function(obj,dateFormat,ignoreFunction){
	    	var type=typeof(obj),callee=arguments.callee,jsonElements,el;
	        if(obj===null || type==='undefined' || (type==='number' && (isNaN(obj) || obj===Infinity))){
	            return "null";
	        }else if(type==='number' || type==='boolean'){
	            return obj.toString();
	        }else if(type==='string'){
	            return ['"',obj.replace(/\\|\n|\r|\t|\"/g, function(c){
	                return ("\n" === c) ? "\\n" : ("\r" === c) ? "\\r" : ("\t" === c) ? "\\t" : ("\\"===c) ? "\\\\" : ("\""===c) ? '\\"' : "";
	            }),'"'].join('');
	        }else if(obj instanceof Date){
	            return callee(dateFormat?$.dateFormat(dateFormat,obj):obj.getTime());
	        }else if(obj instanceof RegExp){
	            return callee(obj.toString().substring(1,obj.length-1));
	        }else if(obj instanceof Function){
	            return ignoreFunction?'""':callee(obj.toString());
	        }else if(obj instanceof Array){
	            var jsonElements=[],el=0;
	            for(var i=0,l=obj.length;i<l;i++){
	                jsonElements[el++]=callee(obj[i],dateFormat,ignoreFunction);
	            }
	            return ['[',jsonElements.join(','),']'].join('');
	        }else{//Object
	            var jsonElements=[],el=0;
	            for(var n in obj){
	                jsonElements[el++]=['"',n,'":',callee(obj[n],dateFormat,ignoreFunction)].join('');
	            }
	            return ['{',jsonElements.join(','),'}'].join('');
	        }
	    },
	    /**
	     * 把表格行序列化成请求字符串;
	     * 一行一个字符串，用splitor分隔，splitor默认是',',
	     * 所有行连接成name=rowstring1&name=rowstring2的格式
	     * table:<table>|<tbody>结点
	     * rowsplitor:'',//行分隔符，指定此参数的情况下请求字符串只有一个name=
	     * rowfilter用于决定序列化哪些行，返回true表示接受，false是拒绝序列化
	     *      参数：i:从0开始计数的第i行，row：第i行的tr对象
	     * tdfilter用于决定序列化哪些列，返回true表示接受，false是拒绝序列化
	     *      参数：i:从0开始计数的第i列，td：第i列的td对象
	     */
	    serializeTr:function(args){
	        //table,splitor,name,rowfilter,tdfilter
	        var deffilter=function(){return true;};
	        rowfilter=args.rowfilter||deffilter;
	        tdfilter=args.tdfilter||deffilter;
	        var serilized=[];
	        $('tr',args.table).each(function(i,row){
	            if(trfilter(i,row)){
	                var r=[];
	                $('td',row).each(function(i,td){
	                    if(tdfilter(i,td)){
	                       r[r.length]=td.text();
	                    }
	                });
	                if(r.length){
	                   serilized[serilized.length]=r.join(splitor||',');
	                }
	            }
	        });
	        var result=[],name=args.name;
	        var rowsplitor=args.rowsplitor;
	        if(rowsplitor){
	            $.each(serilized,function(i,row){
	                result[result.length]=row;
	            });
	            return [name,result.join(rowsplitor)].join('=');
	        }else{
	            $.each(serilized,function(i,row){
	                result[result.length]=[name,'=',row].join('');
	            });
	            return result.join('&');
	        }
	    },
		/**
		 * 用户登出
		 * @param args
		 */
	    logout:function(args){$.get($.concatUrl('/','logout'),function(){delete $.loggedUserid.userid;$.cookie($.loggedUserHeader,null);$.initLoginView(args);});}
	});
})(jQuery);
