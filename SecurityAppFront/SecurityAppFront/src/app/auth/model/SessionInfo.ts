export interface SessionInfo {
  sessionId: string;
  createdAt: string; // ili Date ako ga parsiraš
  lastActivity: string;
  ipAddress?: string;
  userAgent?: string;
}