package net.den3.hackathonWeb.Store;

import net.den3.hackathonWeb.Entity.Facility;
import net.den3.hackathonWeb.Entity.Post;
import net.den3.hackathonWeb.Main;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

public class PostStore implements IPostStore{
    final Jedis jedis = Main.jedis;

    List<Post> posts;

    public PostStore(){
        posts = _getPosts();
    }

    @Override
    public void addPost(Post post) {
        jedis.rpush("posts", post.getUUID());
        String key = "facility."+post.getUUID();
        jedis.expire(key,60*60);

        jedis.rpush(key,post.getFacility().name());
        jedis.expire(key,60*60);

        key = "time."+post.getUUID();
        jedis.rpush(key,post.getTime());
        jedis.expire(key,60*60);

        key = "user."+post.getUUID();
        jedis.rpush(key,post.getUserID());
        jedis.expire(key,60*60);

        key = "date."+ post.getDate();
        jedis.rpush(key,String.valueOf(post.getDate()));
        jedis.expire(key,60*60);

        this.posts.add(post);
    }

    private List<Post> _getPosts(){
        List<Post> posts = new ArrayList<>();
        long size = jedis.llen("posts");

        for (long i = 0; i < size; i++) {
            String key = jedis.lindex("posts",i);
            String user = jedis.get("user."+key);
            String time = jedis.get("time."+key);
            String facility = jedis.get("facility."+key);
            Long date = Long.parseLong(jedis.get("date."+key));
            if(user == null || time == null || facility == null){
                continue;
            }
            posts.add(new Post(user, Facility.valueOf(facility),time,date));
        }
        return posts;
    }

    @Override
    public List<Post> getPosts() {
        return this.posts;
    }
}
