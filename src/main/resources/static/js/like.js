function like(btn, entityType, entityId, targetUserId) {
    $.post(
      CONTEXT_PATH + "/discuss/like",
      {"entityType": entityType, "entityId": entityId, "targetUserId": targetUserId},
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