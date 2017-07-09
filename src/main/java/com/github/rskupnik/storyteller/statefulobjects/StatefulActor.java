package com.github.rskupnik.storyteller.statefulobjects;

import com.github.rskupnik.storyteller.statefulobjects.objects.Actor;
import com.github.rskupnik.storyteller.statefulobjects.states.ActorState;
import com.github.rskupnik.storyteller.structs.StatefulObject;

public class StatefulActor extends StatefulObject<Actor, ActorState> {

    public StatefulActor(Actor object, ActorState state) {
        super(object, state);
    }
}
