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

执行结果在完成推送后补充。

## 后续事项

如果检测到的阿里云密钥仍有效，仓库所有者应在阿里云撤销或轮换，并更新实际使用它的服务配置。本次 Git 操作不能验证或撤销云端密钥。旧提交可能仍留在本地 reflog 中；本次处理不会删除本地备份或清理 reflog。

以后提交前应检查暂存区文件列表，避免提交数据库备份、生产配置或其他敏感数据。

参考：[GitHub 推送保护官方说明](https://docs.github.com/en/code-security/how-tos/secure-your-secrets/work-with-leak-prevention/push-protection-on-the-command-line)。
