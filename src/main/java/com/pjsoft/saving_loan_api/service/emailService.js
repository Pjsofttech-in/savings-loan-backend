// src/services/emailService.js
import apiClient from "./apiClient";

export async function sendEmailNotification({ recipientEmail, subject, templateType, data }) {
  try {
    const payload = {
      email: recipientEmail,
      subject,
      template: templateType, // "REGISTRATION" | "PAYMENT" | "SHARES" | "SAVINGS"
      details: data,
      timestamp: new Date().toISOString()
    };

    // Calls your Spring Boot backend email controller
    const response = await apiClient.post('/api/notifications/send-email', payload);
    return response.data;
  } catch (error) {
    console.error("Email notification dispatch simulated / failed:", error);
    // Fallback log so system flow remains uninterrupted
    return { success: true, simulated: true };
  }
}