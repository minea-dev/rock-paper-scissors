import { Component, OnInit } from '@angular/core';
import { NavigationService } from '../../services/navigation.service';
import { GameService } from '../../services/game.service';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-link-generate',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './link-generate.component.html'
})
export class LinkGenerateComponent implements OnInit {
  link: string | null = null;
  linkCopied: boolean = false;

  constructor(
    private gameService: GameService,
    private navigationService: NavigationService
  ) {}

  ngOnInit(): void {
    this.generateLink();
  }

  generateLink(): void {
    const gameId = localStorage.getItem('gameId');
    if (gameId) {
      this.link = this.gameService.generateSharedGameLink(gameId);
    }
  }

  copyLink(): void {
    if (this.link) {
      if (window.location.protocol === "http:") {
        const textArea = document.createElement('textarea');
        textArea.value = this.link;
        document.body.appendChild(textArea);
        textArea.select();
        document.execCommand('copy');
        document.body.removeChild(textArea);

        this.linkCopied = true;
        setTimeout(() => {
          this.linkCopied = false;
        }, 2000);

      } else {
        navigator.clipboard.writeText(this.link).then(() => {
          this.linkCopied = true;
          setTimeout(() => {
            this.linkCopied = false;
          }, 2000);
        }).catch(err => {
          console.error('Failed to copy link: ', err);
        });
      }
    }
  }

  playGame(): void {
    this.navigationService.navigateTo('/play');
  }
}
