class Solution {
    public:
        ListNode* deleteDuplicates(ListNode* head){
            if(!head){
                return head; //return if head is null
            }

            ListNode* temp,cur=head;
            //next node value is not null
            while(cur->next){
                if(cur->val == cur->next->val){
                    //connect cur.next to next.next
                    cur->next = cur->next->next;                   
                }
                else{
                    //move cur point to next node
                    cur = cur->next; 
                }
            }
            return head;
        }
}

//Time O(n)
//Space O(1)