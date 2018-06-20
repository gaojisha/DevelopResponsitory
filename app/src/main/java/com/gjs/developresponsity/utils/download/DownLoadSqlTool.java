package com.gjs.developresponsity.utils.download;

import android.util.Log;

import com.gjs.developresponsity.database.UserDao;
import com.gjs.developresponsity.model.DownLoadInfo;
import com.gjs.developresponsity.model.User;

import java.util.ArrayList;
import java.util.List;

import static com.gjs.developresponsity.App.userDao;

/**
 * <pre>
 *     author  : gaojisha
 *     e-mail  : gaojisha@feinno.com
 *     time    : 2018/06/13
 *     desc    : 必须说明该类的作用
 *     version : 1.0
 * </pre>
 */
public class DownLoadSqlTool {

    /**
     * 创建下载的具体信息
     */
    public void insertInfos(List<DownLoadInfo> infos) {
        for (DownLoadInfo info : infos) {
            User user = new User(null, info.getThreadId(), info.getStartPos(), info.getEndPos(), info.getCompeleteSize(), info.getUrl());
            userDao.insert(user);
        }
    }

    /**
     * 得到下载具体信息
     */
    public List<DownLoadInfo> getInfos(String urlstr) {
        List<DownLoadInfo> list = new ArrayList<DownLoadInfo>();
        List<User> list1 = userDao.queryBuilder().where(UserDao.Properties.Url.eq(urlstr)).build().list();
        for (User user : list1) {
            DownLoadInfo infoss = new DownLoadInfo(
                    user.getThread_id(), user.getStart_pos(), user.getEnd_pos(),
                    user.getCompelete_size(), user.getUrl());
            Log.d("main-----", infoss.toString());
            list.add(infoss);
        }

        return list;
    }

    /**
     * 更新数据库中的下载信息
     */
    public void updataInfos(int threadId, int compeleteSize, String urlstr) {
        User user = userDao.queryBuilder()
                .where(UserDao.Properties.Thread_id.eq(threadId), UserDao.Properties.Url.eq(urlstr)).build().unique();
        user.setCompelete_size(compeleteSize);
        userDao.update(user);
    }

    /**
     * 下载完成后删除数据库中的数据
     */
    public void delete(String url) {
        userDao.deleteAll();
    }

}

