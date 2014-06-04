
var note=document.getElementById("note");
//插入普通文本
function insertText(text) {
	note.focus();
	var sel,
	range;
	sel = window.getSelection();
	range = sel.getRangeAt(0);
	range.deleteContents();

	var el = document.createElement("div");
	el.innerHTML = text;
	var frag = document.createDocumentFragment(),
	node,
	lastNode;
	while ((node = el.firstChild)) {
		lastNode = frag.appendChild(node);
	}
	range.insertNode(frag);

	if (lastNode) {
		range = range.cloneRange();
		range.setStartAfter(lastNode);
		range.collapse(true);
		sel.removeAllRanges();
		sel.addRange(range);
	}
}
//插入图片
function insertImage(src) {
	var imageNode = document.createElement("img");
	imageNode.src = src;
	insertNode(imageNode);
}
//插入手写图片
function insertHandWriteImage(src){
	var imageNode = document.createElement("img");
	imageNode.src = src;
	imageNode.style.width="50px";
	imageNode.style.height="50px";
	insertNode(imageNode);
}
//插入DOM节点
function insertNode(newNode) {
	note.focus();
	var sel = window.getSelection();
	var range = sel.getRangeAt(0);
	range.collapse(false);
	newNode.style.margin="2px";
	range.insertNode(newNode);
	moveCaret(newNode, sel);
}
//移动光标位置
function moveCaret(node, sel) {
	var range = sel.getRangeAt(0);
	range.setStartAfter(node);
	range.setEndAfter(node);
	range.collapse(false);
	sel.removeAllRanges();
	sel.addRange(range);
}

