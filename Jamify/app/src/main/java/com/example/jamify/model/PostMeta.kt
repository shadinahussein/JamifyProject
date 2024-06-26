package com.example.jamify.model

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

// Firebase insists we have a no argument constructor
data class PostMeta(
    // Auth information
    var ownerName: String = "",
    var ownerUid: String = "",
    val postId: String = "",        //ik we have photouuid for storage, should we
                                    //use a id for the post meta too?
    var photoUuid : String = "",
    var songTitle : String = "",
    var byteSize : Long = 0L,
    var caption: String = "",
    var songId : Long = 0L,
    val private: Boolean = false,
    // Written on the server
    @ServerTimestamp val timeStamp: Timestamp? = null,
    // firestoreID is generated by firestore, used as primary key
    @DocumentId var firestoreID: String = ""
)