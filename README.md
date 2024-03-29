Frameworkを用いた小規模Webアプリケーションの開発
---


このとき，アプリケーションに必要とされる機能にはリンク，あるいはボタンの処理を辿って到達可能となるよう，適切にリンクやボタンを配置すること．
すなわち，トップページを除いて，URLをブラウザに直接打ち込まなければ到達できないページがないようにせよ．

### 必須機能
- [S-1.1] 時間管理アプリケーション
    - データはサーバーに保持されない
    - タスクに取り組む時間を計るタイマー
    - 休憩時間を計るタイマー
- [S-1.2] 一覧表示機能および簡単な検索機能を持つ
    - ユーザはタスクの一覧を取得可能である．
    - ユーザは未完了状態のタスクのみについて，一覧を取得可能である．
- [S-1.3] ログイン機構を持つ
    - ユーザは初回利用時に登録作業を行い，アカウント名およびパスワードをシステムに登録する．
    - 二度目以降の利用時には，登録したアカウント名およびパスワードでログインし，サービスを使用する．
    - すでに登録済みのユーザ名と同じユーザ名では新規登録ができない．
    - ユーザは自分が登録したタスクのみ閲覧，編集が可能である．
    - パスワードは平文でデータベースに保存されず，必ず不可逆にダイジェスト化される．
- [S-1.4] 簡単なアカウント管理機能を持つ
    - ユーザはパスワードを変更することができる．
    - ユーザは退会（アカウント消去）することができる．


### 拡張機能
#### Advanced機能の追加
- [S-2.1] タスクに優先度や締切の概念を追加し，優先度に応じた強調表示，締切までのカウントダウンなどを表示する．
- [S-2.2] 「定期タスク」（毎週金曜はカレーを作るなど）に対応せよ．
- [S-2.3] タグやカテゴリにより，タスクをグルーピングする機能を追加し，検索機能を強化する．
- [S-2.4] タスクの増加に対して，1ページあたり10件程度の表示を複数ページにわたって表示するなど，ページネーションの機能を追加．
- [S-2.5] 複数アカウントによるタスクの共有機能．

