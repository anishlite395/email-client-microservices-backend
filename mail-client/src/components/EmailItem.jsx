export default function EmailItem({email,selected,onSelect,onOpen}){

    console.log(email);

    return(
        <div className={`email-item ${email.seen ? 'read':'unread'}`} onClick={onOpen}>
            <input type="checkbox" checked={selected} onChange={e => onSelect(email.uid,e.target.checked)} onClick={e => e.stopPropagation()} />
            <span className="email-from">{email.from}</span>
            <span className="email-subject">{email.subject}</span>
            {email.attachments && email.attachments.length > 0 && <span className="email-attachment">📎</span>}
            <span className="email-date">{email.sentDate}</span>
        </div>
)
}
    