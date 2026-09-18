# 2026-09-18 GitHub 推送保护问题记录

## 问题

向 `https://github.com/yjhaes/PlayEdu` 推送 `main` 时，GitHub 返回 `GH013: Repository rule violations` 和 `Push cannot contain secrets`，拒绝更新远程分支。

被拒绝的提交为 `69d11ed4ea377697e6b48fe4963eb4acb755bb36`（`Harden point-code imports and learning-duration updates`）。GitHub 在 `.scratch/points-system/evidence/local-playedu-pre-points-migration-20260911.sql` 第 52、227 行检测到 Alibaba Cloud AccessKey ID，在第 227 行检测到 AccessKey Secret。本记录不包含密钥值或放行链接。

## 原因

本地数据库备份被加入了最新提交。工作区干净只表示没有未提交的变化，不表示已经提交的文件没有敏感信息；忽略规则也不会自动移除已跟踪文件。

## 处理

1. 使用 `git rm --cached` 取消该 SQL 备份的跟踪，保留本地文件。
2. 在 `.scratch/points-system/evidence/.gitignore` 中添加该文件的精确路径规则。
3. 使用 `git commit --amend --no-edit` 修正引入备份的最新提交，保留其他代码改动。不能仅新增删除文件的提交，因为先前提交中的密钥仍会被扫描。
4. 检查备份不再存在于当前提交及 `main` 的可达历史中，确认本地文件存在且被忽略，然后执行普通推送 `git push origin main`，不绕过推送保护、不强制推送。

## 验证与结果

- 已修正原提交为 `57dd6b9`；与原提交比较，仅移除该备份、增加忽略规则和本记录，其他代码改动保持一致。
- `git ls-files` 确认备份不再被跟踪，`git log main -- <备份路径>` 无输出，确认它不在 `main` 的可达历史中。
- `Test-Path` 确认本地备份仍存在，`git check-ignore -v` 确认命中新增的忽略规则。
- 首次推送长期没有返回结果，检查远程分支仍为 `da45a8355da31f80611a08456afbc8f397f926c5` 后中止该尝试。随后重试遇到 `OpenSSL SSL_connect: SSL_ERROR_SYSCALL`；再次重试开始上传，但速度持续降至约 4 KB/s，因此中止并尝试调整传输设置。
- 使用仅对本次命令生效的 `http.version=HTTP/1.1`、`http.postBuffer=33554432` 和低速超时设置重试。数据打包完成后仍长时间没有返回远程接收结果；只读核对远程 `main` 仍为上述原提交后中止。没有修改全局 Git 配置，没有强制推送，没有绕过 GitHub 推送保护。
- 最终状态：备份移除与忽略规则已完成，本记录已保存；远程推送尚未完成。网络恢复后执行 `git push origin main`。尚不能声称修正后的提交已经通过 GitHub 推送保护检查。
- 本次仅修改 Git 跟踪规则与文档，未修改应用代码，因此未重新运行应用构建或测试。

## 后续事项

如果检测到的阿里云密钥仍有效，仓库所有者应在阿里云撤销或轮换，并更新实际使用它的服务配置。本次 Git 操作不能验证或撤销云端密钥。旧提交可能仍留在本地 reflog 中；本次处理不会删除本地备份或清理 reflog。

以后提交前应检查暂存区文件列表，避免提交数据库备份、生产配置或其他敏感数据。

参考：[GitHub 推送保护官方说明](https://docs.github.com/en/code-security/how-tos/secure-your-secrets/work-with-leak-prevention/push-protection-on-the-command-line)。
