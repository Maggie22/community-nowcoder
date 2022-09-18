$(function (){
    $("#topBtn").click(setTop);
    $("#recommendBtn").click(setRecommend);
    $("#deleteBtn").click(deletePost);
})

function setTop(){
    $.post(
        CONTEXT_PATH + "/discuss/setTop",
        {"postId" : $("#postId").val()},
        function (data){
            data = $.parseJSON(data);
            if(data.code == 200){
                // 操作成功
                $("#topBtn").attr("disabled", "disabled");
            }
            alert(data.msg);
        }
    )
}

function setRecommend(){
    $.post(
        CONTEXT_PATH + "/discuss/setRecommend",
        {"postId" : $("#postId").val()},
        function (data){
            data = $.parseJSON(data);
            if(data.code == 200){
                // 操作成功
                $("#topBtn").attr("disabled", "disabled");
            }
            alert(data.msg);
        }
    )
}

function deletePost(){
    $.post(
        CONTEXT_PATH + "/discuss/setInvalidate",
        {"postId" : $("#postId").val()},
        function (data){
            data = $.parseJSON(data);
            if(data.code == 200){
                // 操作成功
                $("#topBtn").attr("disabled", "disabled");
                alert(data.msg);
                location.href = CONTEXT_PATH + "/index";
            }else {
                alert(data.msg);
            }
        }
    )
}

function like(btn, entityType, entityId, targetUserId, discussPostId) {
    $.post(
        CONTEXT_PATH + "/discuss/like",
        {"entityType": entityType, "entityId": entityId, "targetUserId": targetUserId, "discussPostId": discussPostId},
        function (data) {
            data = $.parseJSON(data);
            if(data.code === 0){
                // 操作成功
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus === 1 ? "已赞" : "赞");
            }else{
                // 操作失败
                alert(data.msg);
            }
        }
    );
}