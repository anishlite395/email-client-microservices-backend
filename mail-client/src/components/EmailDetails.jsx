import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import '../styles/EmailDetails.css';
import { fetchEmailById } from "../api/mailApi";

export default function EmailDetails(){

    const { id } = useParams();
    const navigate = useNavigate();
    const token = localStorage.getItem("token");

    const [email,setEmail] = useState(null);

    useEffect(() => {
        loadEmail();
    }, [id]);

    const loadEmail = async () => {
        try{
            const res = await fetchEmailById(id);
            setEmail(res.data);
        }catch(error){
            console.error(error);
        }
    }

    const handleReply = () => {
        console.log(email);
        navigate("/compose",{
            state: {
                reply: {
                to: email.from,
                subject: email.subject.startsWith("Re:")
                 ? email.subject
                 :`Re: ${email.subject}`,
                body:`\n\n--- Original Message---\nFrom: ${email.from}\nDate:${email.sentDate}\n\n${email.body}`,
                inReplyToMessageId: email.messageId,
                references: email.references ? email.references.split(",") : [email.messageId]
            }
        }
    });
    };

    if(!email) return <p>Loading...</p>


    return(
        <div className="email-details">
            <button className="back-btn" onClick={() => navigate(-1)}>
                 ← Back
            </button>

            <h2>{email.subject}</h2>

            <div className="email-meta">
                <p><strong>From:</strong>{email.from}</p>
                <p><strong>To:</strong>{email.to}</p>
                <p><strong>Date:</strong>
                {email.sentDate ? new Date(email.sentDate).toLocaleString():""}
                </p>
            </div>

            <hr />

            <div className="email-body">
                {email.body}
            </div>

            {email.attachments && email.attachments.length >0 && (
                <div className="email-attachments">
                    <h4>Attachments</h4>
                    <ul>
                        {email.attachments.map((att,index) => {
                            return(
                            <li key={index}>
                                <a href={`data:${att.mimeType};base64,${att.content}`}
                                download={att.fileName}>
                                    📎 {att.fileName}
                                </a>

                            </li>
                            );
                        })}
                    </ul>
                </div>
            )}
            <div className="reply-container">
                <button className="reply-btn" onClick={handleReply}>
                    Reply
                </button>
            </div>
        </div>
    )
}