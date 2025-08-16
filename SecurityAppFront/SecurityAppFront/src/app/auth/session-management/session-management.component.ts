import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth.service';
import { SessionInfo } from '../model/SessionInfo';

@Component({
  selector: 'app-session-management',
  templateUrl: './session-management.component.html',
  styleUrls: ['./session-management.component.css']
})
export class SessionManagementComponent implements OnInit{


  sessions: SessionInfo[] = []; 

  constructor(private authSerivce:AuthService){}

  ngOnInit() {
  const user = this.authSerivce.getCurrentUser();
  if (user) {
    this.authSerivce.getActiveSessions(user.userId).subscribe(sessions => {
      this.sessions = sessions;
    });
  }
}

logoutSession(sessionId: string) {
  this.authSerivce.revokeSession(sessionId).subscribe(() => {
    this.sessions = this.sessions.filter(s => s.sessionId !== sessionId);
  });
}

  
}
