package me.xepos.rpg.classes.skills;

import me.xepos.rpg.entities.Follower;

import java.util.List;

public interface IFollowerContainer {
    List<Follower> getFollowers();

    void addFollower(Follower follower);

    int getFollowerCount();

    byte getMaxFollowers();

    void setMaxFollowers(byte maxFollowers);
}
