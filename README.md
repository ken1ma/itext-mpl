This repository contains iText under MPL license, for those who still need the legacy version.
Newer versions are under AGPL license and available at https://github.com/itext/itextpdf


# How this repository has been created

1. Clone the repository that contains both MPL and AGPL code.

		$ git clone https://github.com/itext/itextpdf.git itext-mpl
		Cloning into 'itext-mpl'...
		remote: Counting objects: 79447, done.
		remote: Total 79447 (delta 0), reused 0 (delta 0), pack-reused 79447
		Receiving objects: 100% (79447/79447), 111.38 MiB | 4.82 MiB/s, done.
		Resolving deltas: 100% (40960/40960), done.
		Checking out files: 100% (3348/3348), done.

		$ cd itext-mpl

2. Detach from the upstream

		$ git remote remove origin

3. Create `master` branch from the last MPL version.

		$ git checkout -b master 2.1.7
		Switched to a new branch 'master'

4. Delete the AGPL code

		$ git branch -D develop
		Deleted branch develop (was 50390a036).

		$ git tag -d `git tag | grep 5.`
		Deleted tag '5.0.0' (was 173454923)
		Deleted tag '5.0.1' (was 907f8d0c5)
		Deleted tag '5.0.2' (was 9ea15a2be)
		Deleted tag '5.0.3' (was 617a52b17)
		Deleted tag '5.0.4' (was 930670281)
		Deleted tag '5.0.5' (was 907a623d1)
		Deleted tag '5.0.6' (was 2baae287c)
		Deleted tag '5.1.0' (was e7c3f9d13)
		Deleted tag '5.1.1' (was b7d57a999)
		Deleted tag '5.1.2' (was a81d3523b)
		Deleted tag '5.1.3' (was fa56d71fb)
		Deleted tag '5.2.0' (was da7827c33)
		Deleted tag '5.2.1' (was 02481fcae)
		Deleted tag '5.3.0' (was 800730103)
		Deleted tag '5.3.1' (was 0c23675d7)
		Deleted tag '5.3.2' (was 8d0ffd4f2)
		Deleted tag '5.3.3' (was cbbfaa504)
		Deleted tag '5.3.4' (was 193a319c4)
		Deleted tag '5.3.5' (was e53227a2a)
		Deleted tag '5.4.0' (was 6b4d1c857)
		Deleted tag '5.4.1' (was 889f3487e)
		Deleted tag '5.4.2' (was 1cb56dfc5)
		Deleted tag '5.4.3' (was a1f46cbe8)
		Deleted tag '5.4.4' (was b0c8c06a4)
		Deleted tag '5.4.5' (was 8ee7dfcbd)
		Deleted tag '5.5.0' (was 84f3347fb)
		Deleted tag '5.5.1' (was 9e443cde7)
		Deleted tag '5.5.10' (was 5a9810af6)
		Deleted tag '5.5.11' (was 82779ced3)
		Deleted tag '5.5.12' (was 49099c61d)
		Deleted tag '5.5.13' (was 2da28f115)
		Deleted tag '5.5.2' (was 2abcc7469)
		Deleted tag '5.5.3' (was fb9a12a62)
		Deleted tag '5.5.4' (was 5860d2278)
		Deleted tag '5.5.5' (was 8659d8bcb)
		Deleted tag '5.5.6' (was 73a7fcdbb)
		Deleted tag '5.5.7' (was f3357c378)
		Deleted tag '5.5.8' (was 770df6fe2)
		Deleted tag '5.5.9' (was 523555ba9)
		Deleted tag 'itextg-5.4.3' (was fef04fe38)
		Deleted tag 'itextg-5.4.4' (was 83b1918b3)
		Deleted tag 'itextg-5.4.5' (was e7e8dc002)
		Deleted tag 'itextg-5.5.0' (was 6aba02c58)
		Deleted tag 'itextg-5.5.1' (was b2df3df0e)
		Deleted tag 'itextg-5.5.10' (was 2397d04fc)
		Deleted tag 'itextg-5.5.2' (was 59d9f89f5)
		Deleted tag 'itextg-5.5.3' (was 3e733714f)
		Deleted tag 'itextg-5.5.4' (was 17bf720af)
		Deleted tag 'itextg-5.5.5' (was 96c1179e0)
		Deleted tag 'itextg-5.5.6' (was 1bd1fbd7c)
		Deleted tag 'itextg-5.5.7' (was 058049bb4)
		Deleted tag 'itextg-5.5.8' (was 13edf86bd)
		Deleted tag 'itextg-5.5.9' (was cc5fab759)

5. Push to an empty github repository

		$ git remote add origin https://github.com/ken1ma/itext-mpl.git

		$ git push -u origin master
		Counting objects: 27208, done.
		Delta compression using up to 8 threads.
		Compressing objects: 100% (5573/5573), done.
		Writing objects: 100% (27208/27208), 14.63 MiB | 5.39 MiB/s, done.
		Total 27208 (delta 14696), reused 26845 (delta 14510)
		remote: Resolving deltas: 100% (14696/14696), done.
		To https://github.com/ken1ma/itext-mpl.git
		 * [new branch]          master -> master
		Branch 'master' set up to track remote branch 'master' from 'origin'.

		$ git push --tags
		Counting objects: 48, done.
		Delta compression using up to 8 threads.
		Compressing objects: 100% (48/48), done.
		Writing objects: 100% (48/48), 5.98 KiB | 1.99 MiB/s, done.
		Total 48 (delta 0), reused 48 (delta 0)
		To https://github.com/ken1ma/itext-mpl.git
		 * [new tag]             1.0.0 -> 1.0.0
		 * [new tag]             1.0.1 -> 1.0.1
		 * [new tag]             1.0.2 -> 1.0.2
		 * [new tag]             1.0.3 -> 1.0.3
		 * [new tag]             1.1.0 -> 1.1.0
		 * [new tag]             1.1.1 -> 1.1.1
		 * [new tag]             1.1.2 -> 1.1.2
		 * [new tag]             1.1.3 -> 1.1.3
		 * [new tag]             1.1.4 -> 1.1.4
		 * [new tag]             1.2.0 -> 1.2.0
		 * [new tag]             1.2.1 -> 1.2.1
		 * [new tag]             1.2.2 -> 1.2.2
		 * [new tag]             1.2.3 -> 1.2.3
		 * [new tag]             1.2.4 -> 1.2.4
		 * [new tag]             1.3.0 -> 1.3.0
		 * [new tag]             1.3.1 -> 1.3.1
		 * [new tag]             1.3.2 -> 1.3.2
		 * [new tag]             1.3.3 -> 1.3.3
		 * [new tag]             1.3.4 -> 1.3.4
		 * [new tag]             1.3.5 -> 1.3.5
		 * [new tag]             1.3.6 -> 1.3.6
		 * [new tag]             1.4.0 -> 1.4.0
		 * [new tag]             1.4.1 -> 1.4.1
		 * [new tag]             1.4.2 -> 1.4.2
		 * [new tag]             1.4.3 -> 1.4.3
		 * [new tag]             1.4.4 -> 1.4.4
		 * [new tag]             1.4.5 -> 1.4.5
		 * [new tag]             1.4.6 -> 1.4.6
		 * [new tag]             1.4.7 -> 1.4.7
		 * [new tag]             1.4.8 -> 1.4.8
		 * [new tag]             1.4.9 -> 1.4.9
		 * [new tag]             2.0.0 -> 2.0.0
		 * [new tag]             2.0.1 -> 2.0.1
		 * [new tag]             2.0.2 -> 2.0.2
		 * [new tag]             2.0.3 -> 2.0.3
		 * [new tag]             2.0.4 -> 2.0.4
		 * [new tag]             2.0.5 -> 2.0.5
		 * [new tag]             2.0.6 -> 2.0.6
		 * [new tag]             2.0.7 -> 2.0.7
		 * [new tag]             2.0.8 -> 2.0.8
		 * [new tag]             2.1.0 -> 2.1.0
		 * [new tag]             2.1.1 -> 2.1.1
		 * [new tag]             2.1.2 -> 2.1.2
		 * [new tag]             2.1.3 -> 2.1.3
		 * [new tag]             2.1.4 -> 2.1.4
		 * [new tag]             2.1.5 -> 2.1.5
		 * [new tag]             2.1.6 -> 2.1.6
		 * [new tag]             2.1.7 -> 2.1.7

