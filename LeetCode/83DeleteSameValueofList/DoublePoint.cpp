//用一个指针去找第一个不重复的结点，然后让前面的链尾指针去链接到这个结点，然后此结点变为链尾,直到找到链表尾部

struct ListNode* deleteDuplicates(Struct ListNode* head){
    struct ListNode* posnode = head;
    if(head == NULL){
        return NULL;
    }
    struct ListNode* rearnode = posnode->next;
    if(rearnode == NULL) //only one element
    {
        return head;
    }
    while(1)
    {
        while(posnode->val == rearnode->val || posnode == rearnode) //find first different node
        {
            rearnode = rearnode->next;
            if(rearnode == NULL)
            {
                break;
            }
        }
        posnode->next = rearnode;
        if(rearnode==NULL)
        {
            break;
        }
        posnode = rearnode;
    }
    return head;
}