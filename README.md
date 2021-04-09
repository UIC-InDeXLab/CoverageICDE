# CoverageICDE
This repository contains the code for the ICDE 2019 paper where we introduced the notion of **Coverage** over low-dimensional categorical attributes.

The implementation is (mainly) done by Zhongjun Jin. Please feel free to contact the authors if you have any questions.

# Abstract
Data analysis impacts virtually every aspect of our society today. Often, this analysis is performed on an existing dataset, possibly collected through a process that the data scientists had limited control over. The existing data analyzed may not include the complete universe, but it is expected to cover the diversity of items in the universe. Lack of adequate coverage in the dataset can result in undesirable outcomes such as biased decisions and algorithmic racism, as well as creating vulnerabilities such as opening up room for adversarial attacks.

In this work, we assess the coverage of a given dataset over multiple categorical attributes. We first provide efficient techniques for traversing the combinatorial explosion of value combinations to identify any regions of attribute space not adequately covered by the data. Then, we determine the least amount of additional data that must be obtained to resolve this lack of adequate coverage.

# Publications to cite:
[1] Abolfazl Asudeh, Zhongjun Jin, H. V. Jagadish. **Assessing and Remedying Coverage for a Given Dataset**. ICDE, 2019.

[2] (Demo) Zhongjun Jin, Mengjing Xu, Chenkai Sun, Abolfazl Asudeh, H. V. Jagadish. **MithraCoverage: A System for Investigating Population Bias for Intersectional Fairness**. SIGMOD, 2020, ACM. 
