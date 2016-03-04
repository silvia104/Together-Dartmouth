package edu.dartmouth.cs.together;

import edu.dartmouth.cs.together.data.Message;

/**
 * Created by foxmac on 16/3/2.
 */
public interface InsertNewMessage {
        // update ListView
        void onPostExecute(Message msg);
}
