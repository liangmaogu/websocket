/**
 * 动态引入css文件
 * @param {} url css文件的url
 */
function addCssByLink(url) {
	var link = document.createElement("link");
	link.setAttribute("rel", "stylesheet");
	link.setAttribute("type", "text/css");
	link.setAttribute("href", url);

	addResourcesToHead(link);
}

/**
 * 动态引入js文件
 * @param {} url js文件的url
 */
function addJsByScript(url) {
	var script = document.createElement("script");
	script.setAttribute("type", "text/javascript");
	script.setAttribute("src", url);
	
	addResourcesToHead(script);
}

/**
 * 动态引入资源文件到head
 * @param {} childNode
 */
function addResourcesToHead(childNode) {
	var heads = document.getElementsByTagName("head");
	if (heads.length){
		heads[0].appendChild(childNode);
	} else {
		document.documentElement.appendChild(childNode);
	}
}

function createSection() {
	var section = document.createElement("section");
	section.innerHTML = "<div><ul id='content'></ul></div>" +
			"<div class='userlist'><ul id='userList'></ul></div>";
	var bodys = document.getElementsByTagName("body");
	if (bodys.length) {
		bodys[0].appendChild(section);
	} else {
		document.documentElement.appendChild(section);
	}
	
	return section;
}

function createFooter() {
	var footer = document.createElement("footer");
	footer.innerHTML = "<input type='radio' id='msgTypeTxt' name='msgType' checked>txt" +
			"<input type='radio' id='msgTypeDraw' name='msgType'>draw<br/>" +
			"<div id='msgCanvas'>" +
			"	<canvas id='userCanvas' width='282' height='124'></canvas>" +
			"	<input id='penRange' class='range' type='range' min='1' max='21' step='2' value='3'>" +
			"	<div id='penContent'>" +
			"		<div id='pen' class='pen'></div>" +
			"	</div><br/>" +
			"	<input id='color' type='color' value='#414de2'>" +
			"	<input id='typePen' type='radio' name='penType' checked>pen" +
			"	<input id='typeEraser' type='radio' name='penType'>eraser<br/>" +
			"	<button id='clearBtn' type='button'>clear</button>" +
			"</div>" +
			"<textarea id='msg'></textarea><br/>" +
			"<button id='sendBtn' type='button' class='send'>send</button>";
	
	var bodys = document.getElementsByTagName("body");
	if (bodys.length) {
		bodys[0].appendChild(footer);
	} else {
		document.documentElement.appendChild(footer);
	}
}

function appendMsgToNode(node, msg) {
	var span = document.createElement("span");
	span.innerText = msg;
	node.appendChild(span);
}


var Notice = {
	support: true,
	permission: false,
	sendFlag: true,
	isOpening: false,
	
	init: function() {
		this.bind();
	},
	
	bind: function() {
		$(document).bind("click", this.checkPermission);
		$(window).bind("blur", function(e) {
			Notice.sendFlag = true;
		});
		
		$(window).bind("focus", function(e) {
			Notice.sendFlag = false;
		});
	},
	
	unbind: function() {
		$(document).unbind("click", this.checkPermission);
	},
	
	checkPermission: function(e) {
		if (Notice.support && !Notice.permission) {
			Notice.showDesktopNotice(true);
		}
	},
	
	showDesktopNotice: function(checkFlag) {
		var myNotifications = window.webkitNotifications; 
		//判断浏览器是否支持webkitNotifications
		if(myNotifications){
			//判断是否获得了权限
			if(this.permission || myNotifications.checkPermission() == 0){
				this.permission = true;
				if (checkFlag || !this.sendFlag) 
					return;
				//实例化通知对象
				var notification = myNotifications.createNotification('/images/notify.png','通知','秘密会所有新消息啦！');
				notification.ondisplay = function(){
					//显示通知前触发事件
				};
				notification.onclose = function(){
					//关闭通知后触发事件
					Notice.isOpening = false;
				};
				notification.show();//显示通知
				this.isOpening = true;
			}else{
				myNotifications.requestPermission();//获取用户权限
			}
		}else{
			this.support = false;
		}
	}
	
}

var Meeting = {
	maxMsg: 512,
	msgType: "txt",
	lastLiCss: "",
	
	init: function() {
		this.counter = $("#counter");
		this.content = $("#content");
		this.userList = $("#userList");
		this.msgText = $("#msg");
		this.msgCanvas = $("#msgCanvas");
		this.msgTypeTxt = $("#msgTypeTxt");
		this.msgTypeDraw = $("#msgTypeDraw");
		
		this._bind();
	},
	
	_bind: function() {
		this.msgTypeTxt.bind("change", function() {
			if (this.checked) {
				Meeting.showMsgText();
			} else {
				Meeting.showMsgCanvas();
			}
		});
		
		this.msgTypeDraw.bind("change", function() {
			if (this.checked) {
				Meeting.showMsgCanvas();
			} else {
				Meeting.showMsgText();
			}
		});
	},
	
	showMsgText: function() {
		Meeting.msgType = "txt",
		Meeting.msgCanvas.hide();
		Meeting.msgText.show();
	},
	
	showMsgCanvas : function() {
		Meeting.msgType = "img";
		Meeting.msgText.hide();
		Meeting.msgCanvas.show();
	},
	
	showMsg: function(from, msgTitle, msg, msgType) {
		if (!msg || (msg.length == 0)) {
			return;
		}
		
		var result = ["<li class='", 
			this.lastLiCss, 
			" ", 
			from, 
			"'>",
			"<span style='color:#39f;font-size: 24px;'>",
			msgTitle, 
			"</span>",
			"<br/>",
			(msgType=="txt" ? "<span style='font-size: 18px;'>&nbsp;&nbsp;" + this.filterMsg(msg) + "</span>" : "<image class='msgImg' src='" + msg + "'>"),
			"</li>"];

		this.content.append(result.join(''));
		if (this.content.children().length > this.maxMsg) {
			$(this.content.children()[0]).remove();
		}
		this.lastLiCss = (this.lastLiCss.length>0 ? "" : "shadow");
		this.content.parent().scrollTop(this.content.get(0).scrollHeight)
	},
	
	sendMsg : function() {
		var msg = this.getMyMsg();
		if (msg.length > 0) {
			Chat.socket.send("msgType:'" + this.msgType + "'&-&msg:" + this.getMyMsg());
			this.msgText.get(0).value = "";
			DrawBoard.clearCanvas();
		}		
	},
	
	getMyMsg : function() {
		if (this.msgType=="txt") {
			return this.msgText.get(0).value;
		} else {
			var m = DrawBoard.board.get(0).toDataURL();
			if (m !== DrawBoard.emptyMsg) {
				return m;
			}
		}
	}, 
	
	showUserList : function(userList) {
		var u = [];
		for ( var ip in userList ) {
			var num = userList[ip];
			u.push("<li>");
			u.push(ip);
			if (num > 1) {
				u.push(" + " + (num-1) + " 个马甲");
			}
			u.push("</li>");
		}
		this.userList.empty().append(u.join(''));
	},
	
	filterMsg : function(msg) {

		return msg.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;')
					.replace(/((http|https):[^ ]*)/ig, "<a target='_blank' href='$1'>$1</a>");
	}
}

var DrawBoard = {
	init: function(options) {
		this.board = $("#" + options.userCanvas);
		this.pen = $("#" + options.pen);
		this.penRange = $("#" + options.penRange);
		this.color = $("#" + options.color);
		this.clearBtn = $("#" + options.clearBtn);
		this.backBtn = $("#" + options.backBtn);
		this.typePen = $("#" + options.typePen);
		this.context = this.board.get(0).getContext("2d");
		this.emptyMsg = DrawBoard.board.get(0).toDataURL();
		this.initPen();
		this._bind();
	},
	
	initPen : function() {
		var pen = DrawBoard.pen, penRange = DrawBoard.penRange.get(0).value,
		left = (21 - penRange)/2;
		pen.css({
			left:left, top:left, width:penRange + "px", height:penRange + "px",
			"-moz-border-radius": penRange/2 + "px",
			"-khtml-border-radius": penRange/2 + "px",
			"-webkit-border-radius": penRange/2 + "px",
			"border-radius": penRange/2 + "px"
		});
	},
	
	_bind: function() {
		this.board.bind("mousedown", this.drawStart)
		.bind("mouseup", this.drawEnd);
		this.penRange.bind("change", this.initPen);
		this.clearBtn.bind("click", this.clearCanvas);
		this.backBtn.bind("click", this.backCanvas);
	},
	
	drawStart: function(e) {
		var cont = DrawBoard.context, x = e.offsetX, y = e.offsetY;
		DrawBoard.curColor = DrawBoard.typePen.get(0).checked ? DrawBoard.color.get(0).value : "#FFFFFF";
		cont.save();
		cont.beginPath();
		cont.moveTo(x, y);

		DrawBoard.board.bind("mousemove", DrawBoard.drawMove);
		e.preventDefault();

	},
	
	drawMove: function(e) {
		var cont = DrawBoard.context, x = e.offsetX, y = e.offsetY;
		cont.lineTo(x, y);

		cont.lineWidth = DrawBoard.penRange.get(0).value;
		cont.lineJoin = "round";
		cont.strokeStyle = DrawBoard.curColor;
		cont.stroke();
	},
	
	drawEnd: function(e) {
		var cont = DrawBoard.context;
		cont.closePath();		
		DrawBoard.board.unbind("mousemove", DrawBoard.drawMove);
	},
	
	clearCanvas: function(e) {
		DrawBoard.context.clearRect( 0, 0, DrawBoard.board.attr("width"), DrawBoard.board.attr("height") );
		DrawBoard.drawEnd();
	},
	
	backCanvas: function(e) {
		DrawBoard.context.restore();
	}
}

var Chat = {
	connect: function(host) {
		if ("WebSocket" in window) {
			Chat.socket = new WebSocket(host);
		} else if ("MozWebSocket" in window) {
			Chat.socket = new MozWebSocket(host);
		} else {
			Meeting.showMsg("sys", "WebSocket is not supported by this browser.");
			return;
		}
		
		Chat.socket.onopen = function() {
			document.getElementById('msg').onkeydown = function(event) {
				if (event.keyCode == 13) {
					Meeting.sendMsg();
				}
			};
		}
		
		Chat.socket.onclose = function() {
			document.getElementById('msg').onkeydown = null;
			Meeting.showMsg("sys", "WebSocket closed.");
		}
		
		Chat.socket.onmessage = function(message) {
			var data = message.data;
			if (data.indexOf("from") > 0) {
				var json = eval("({" + data + "})")
				Meeting.showMsg("server", json.from + ": ", json.msg, json.msgType);
			} else {
				Meeting.showMsg("sys", data);
			}
			
		}
	},
	
	init: function() {
		if (window.location.protocol == "http:") {
			Chat.connect("ws://" + window.location.host + "/websocket/chat.do");
		} else {
			Chat.connect("wss://" + window.location.host + "/websocket/chat.do")
		}
	}
}

/**
 * 测试方法
 */
$(function() {
	addCssByLink("./style/chat.css");
	var section = createSection();
	var footer = createFooter();
	
	Meeting.init();
	Notice.init();
	DrawBoard.init({
		userCanvas: "userCanvas",
		pen: "pen",
		penRange: "penRange",
		color: "color",
		clearBtn: "clearBtn",
		backBtn: "backBtn",
		typePen: "typePen"
	});
	
	Chat.init();
	
	$("#sendBtn").bind("click", function() {
		Meeting.sendMsg();
	});
});
