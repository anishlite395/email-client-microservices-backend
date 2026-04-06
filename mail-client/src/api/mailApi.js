import api from "./axios";


export const sendingMailMultipart = (data) => api.post(`/email/send/multipart`,data);

export const saveDrafts = (data) => api.post(`/drafts`,data);

export const fetchDrafts = () => api.get(`/drafts`);

export const fetchInbox = () => api.get(`/inbox`);

export const fetchDraftsMultipart = () => api.get(`/email/drafts/multipart`);

export const sendingMail = (data) => api.post(`/email/send`,data);

export const fetchEmailById = (id) => api.get(`/inbox/${id}`);

export const deleteEmails = (uids) => api.delete(`/inbox/delete`,{
    data: uids
});

export const fetchSent = () => api.get(`/sent`);

export const deleteSentEmails = (uids) => api.delete(`/sent/delete`,{
    data: uids
});

export const fetchScheduled = () => api.get(`/email/scheduled`);

export const markAsRead = (uid) => api.put(`/sent/read/${uid}`);

export const inboxMarkAsRead = (uid) => api.put(`/inbox/${uid}/read`);

export const deleteDrafts = (uids) => api.delete(`/drafts/delete`,{
    data: uids
});