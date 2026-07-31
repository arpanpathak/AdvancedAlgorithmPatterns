# Appendix: The Roadmap — Remaining Uncovered Files

> The repo holds **91 files** that this book hasn't given a full page yet. They're real problems — mostly medium/hard LeetCode classics and a few research-y extras — and they're the natural next chapters. This roadmap keeps the promise of the [coverage index](appendix-repo-coverage-index.md) honest: *every* file in the repo is accounted for, either by a page, as a variant, or here.

## How to read this roadmap

Each line is a repo file and the pattern family it belongs to — almost all of them are already *half-covered* because the book's pages teach their core engine:

- **Monotonic stack** → [ch08](ch08-stacks/pattern-primer.md)
- **BFS on grids** → [6.10](ch06-graphs/flood-fill.md), [6.14](ch06-graphs/rotting-oranges.md), [17.11](ch17-advanced-graphs/walls-and-gates.md)
- **DP tables** → [ch02](ch02-dynamic-programming/pattern-primer.md)
- **Union-Find** → [6.9](ch06-graphs/redundant-connection.md), [6.11](ch06-graphs/the-earliest-moment-everyone-became-friends.md)
- **Trie / suffix machinery** → [ch13](ch13-tries/pattern-primer.md)
- **Segment / Fenwick trees** → the appendix's Range Query family
- **Greedy scheduling** → [ch11](ch11-greedy/pattern-primer.md)

## By family

### Grid BFS/DFS (the [6.10](ch06-graphs/flood-fill.md)/[6.14](ch06-graphs/rotting-oranges.md) engines)
`SurroundedRegion.kt`, `SurroundedRegionDfs.kt`, `MakingALargeIsland.kt`, `MakingALargeIsland_AnotherApproach.kt`, `IslandPerimeter.kt`, `MaximumNumberOfFishInAGrid.kt`, `ShortestBridge.kt`, `ShortestDistanceFromAllBuildings.kt`, `PacificAtlanticWaterFlow.kt`, `WallsAndGates.kt` (now [17.11](ch17-advanced-graphs/walls-and-gates.md)), `RottingOranges.kt` (now [6.14](ch06-graphs/rotting-oranges.md)), `WordSearch_II.kt`, `ShortestPathInGridWithObstaclesElimination.kt`

### Stack classics ([ch08](ch08-stacks/pattern-primer.md))
`BasicCalculator.kt`, `BasicCalculator_I.kt`, `BasicCalculator_II.kt`, `BasicCalculator_II_ShortCode.kt`, `BasicCalculator_III.kt`, `RemoveAllAdjacentDuplicatesInString.kt`, `AestroidCollisions.kt`, `OnlineStockSpan.kt`, `NumberOfVisiblePeopleInAQueue.kt`, `BuildingsWithAnOceanView.kt`, `ExclusiveTimeOfFunctions.kt`, `OneThreeTwoPattern.kt`, `SumOfSubArrayMinimum.kt`, `SumOfSubArrayRanges.kt`, `RemoveDuplicateLetters.kt`, `SmallestSubsequenceOfDistinctCharacters.kt`, `SimplifyPath.kt`, `RemoveStarsFromString.kt`, `MinimumAddtoMakeParenthesesValid.kt`, `MinimumRemoveToMakeValidParentheses.kt`, `MaximumFrequencyStack.kt`

### DP tables ([ch02](ch02-dynamic-programming/pattern-primer.md))
`BurstBaloons.kt`, `BurstBallonsClean.kt`, `CoinChange_II.kt`, `CoinChange_II_BottomUp.kt`, `CherryPickup.kt`, `CherryPickup_II.kt`, `MinimumPathSum.kt`, `StoneGame.kt`, `SplitArrayLargestSum.kt`, `TargetSum.kt`, `LongestIncreasingSequenceInAMatrix.kt`, `MinimumNumberofIncrementsSubarraysFormaTargetArray.kt`, `PartitionArrayIntoTwoArrayToMinimuzeSumDifference.kt`, `MaximumSumOfNonAdjacentElements.kt` (a [2.17](ch02-dynamic-programming/house-robber.md) rename), `MinCostClimbingStaris.kt`

### String DP ([ch09](ch09-strings/pattern-primer.md) family)
`InterleavingString.kt`, `DeleteOperationsForTwoStrings.kt`, `LongestPalindromicSubsequence.kt`, `LongestPalindromicSubsequence_BottomUp.kt`, `LongestStringChain.kt`, `PalindromePartitioning_II.kt`, `RegularExpressionMatching.kt`, `ShortestCommonSupersequence.kt`, `ShortestCommonSuperSequence_Modular.kt`, `ValidPalindrome_III.kt`, `ValidPalindrome_III_SpaceOptimized.kt`, `WordBreak_II.kt`, `WorlBreak_I_DP.kt`

### Union-Find variants ([6.9](ch06-graphs/redundant-connection.md)/[6.11](ch06-graphs/the-earliest-moment-everyone-became-friends.md))
`AccountMerge.kt`, `DynamicConnectivity.kt`, `NumberOfIsland_II.kt`, `NumerOfIsland_II_Optimized.kt`, `SocialNetworkOperations.kt`, `UnionFind.kt`

### Graphs ([ch06](ch06-graphs/pattern-primer.md)/[ch17](ch17-advanced-graphs/pattern-primer.md))
`BFSCycleDetection.kt`, `CourseSchedule.kt`, `FindLengthOfLongestCycle.kt`, `ParallelCourses.kt`, `ParallelCourses_II.kt`, `ParallelCourses_II_Recursive.kt`, `ParallelCourses_II_FunctionalProgramming.kt`, `MaximumVacationDays.kt`, `CourseWithSemesterConstraint.kt`, `MinimumTimeToFinishBuildByKWorkers.kt`, `FloydWarshallAlgorithm.kt`, `BusRoutes.kt`, `MinimumGeneticMutations.kt`, `MaximumPathQualityOfAGraph.kt`, `PathWithMaximumProbability.kt`, `NColoringGreedy.kt`, `ChromaticNumber.kt`, `ChromaticNumberOptimized.kt`, `TheMaze_III.kt`, `OptimizeWaterDistributionInAVillage.kt`, `ApplySubstitutions.kt`, `StepByStepDirectionsFromANodeToAnother.kt`, `MinimumTimeToCollectAllApplesInATree.kt`, `NetworkDelayTime.kt` (now [6.12](ch06-graphs/network-delay-time.md)), `WordLadder_II.kt`, `WordLadder_II_FinalCutPro.kt`, `EvalualteDivisions.kt`

### Sweep line / intervals ([7.8](ch07-heaps/the-skyline-problem.md)/[11.4](ch11-greedy/meeting-rooms-ii.md))
`MyCalendar.kt`, `MyCalendar_II.kt`, `EmployeeFreeTime.kt`, `MeetingScheduler.kt`, `RescheduleMeetingsforMaximumFreeTime_I.kt`, `HowManyRectanglesOverlaping.kt`, `HowManyRectanglesOverlapSweepLine.kt`, `HowManyRectangleOverlapsIntervalTree.kt`, `RectangeOverlapCountTreeSet.kt`, `MaximumPopulationYear.kt`, `CarPooling.kt`, `CountCollisionsOnARoad.kt`, `RobotBoundedInCircle.kt`, `TextJustification.kt`, `Racecar.kt`, `SlidingPuzzle.kt`, `FindWinnerOnATicTacToeGame.kt`, `DesignTicTacToe.kt`

### Trees ([ch05](ch05-trees/pattern-primer.md))
`LeafSimilar.kt`, `CountGoodNodeInBInaryTree.kt`, `CountNodeEqualsAverage.kt`, `AverageOfLevelsInBinaryTree.kt`, `FindLargestValueInEachTreeRow.kt`, `CheckCompletenessOfBinaryTree.kt`, `BinaryTreeVerticalOrderTraversal.kt`, `BinaryTreeVerticalOrderTraversal_WithoutSorting.kt`, `BoundaryOfBinaryTree.kt`, `MaximumWidthOfBinaryTree.kt`, `MaximumLevelSumOfABinaryTreee.kt`, `SumRootToLeafNumbers.kt`, `PopulatingNextRightPointerInEachNode.kt`, `PopulateNextRightPointersInEachNode_II.kt`, `PopulateNextRightPointersInEachNode_II_Constant.kt`, `ConstructBinaryTreeFromInorderAndPostOrderTraversal.kt`, `ConstructBinaryTreeFromString.kt`, `RecoverATreeFromPreOrderTraversal.kt`, `SerializeAndDeserializeNArrayTree.kt`, `BSTIterator.kt`, `BinarySearchTreeToGreaterSumTree.kt`, `RangeSumOfBST.kt`, `ClosestBinarySearchTreeValue.kt`, `InorderSuccessor.kt`, `ConvertBInarySearchTreeToSortedDoublyLinkedList.kt`, `DeleteNodeinABST.kt`, `RecoverBinarySearchTree.kt`, `UniqueBinarySearchTrees.kt`, `UniqueBinarySearchTrees_II.kt`, `LongestUnivaluePath.kt`, `AllNodesDistanceKinBinaryTree.kt`, `DiameterOfNArrayTree.kt`, `MaximumProductOfSplittedBinaryTree.kt`, `MaximumSumBSTInBinaryTree.kt`, `LongestPathWithDifferentAdjacentCharacters.kt`, `MinimumTimeToCollectAllApplesInATree.kt`

### Linked lists ([ch04](ch04-linked-lists/pattern-primer.md))
`MiddleNode.kt`, `PalindromeLinkedList.kt`, `OddEvenLinkedList.kt`, `OddOrEvenLinkedList.kt`, `RotateList.kt`, `SwapNodesInPairs.kt`, `ReverseNodesInKGroups.kt`, `MergeKSortedList.kt`, `MergeKSortedListHeap.kt`, `MergeKSortedListIterative.kt`, `CopyLinkedListWithRandomPointer.kt`, `IntersectionOfTwoLinkedList.kt`, `MaximumTwinSumOfALinkedList.kt`, `InsertIntoASortedCircularLinkedList.kt`, `InsertIntoASortedCircularList.kt`, `DeleteMiddleNodeOfLinkedList.kt`, `AddTwoNumbers.kt` (now [4.7](ch04-linked-lists/add-two-numbers.md))

### Strings & math ([ch09](ch09-strings/pattern-primer.md)/[ch10](ch10-hash-tables/pattern-primer.md))
`CountAndSay.kt`, `LengthOfLastWord.kt`, `ExcelSheetToColumnNumber.kt`, `GoatLatin.kt`, `GreatestCommonDivisorOfStrings.kt`, `DetectCapital.kt`, `MergeStringAlternatively.kt`, `ReverseVowelOfString.kt`, `ValidWordAbbreviation.kt`, `ValidNumber.kt`, `StringCompression.kt`, `StringCompression_II.kt`, `BreakAPalindrome.kt`, `ShortestWayToFormAString.kt`, `DetermineIfStringsAreClose.kt`, `GroupShiftedStrings.kt`, `UniqueLength3PalindromicSubsequence.kt`, `UniqueSubstringWithEqualDigitFrequency.kt`, `FindUniqueBinaryString.kt`, `CustomSortString.kt`, `CustomSortString_Linear.kt`, `IntegerToEnglishWords.kt`, `AddStrings.kt`, `AddBinary.kt`, `MultiplyStrings.kt`, `DivideTwoIntegers.kt`, `PlusOne.kt`, `ReverseInteger.kt`, `PowerOfTwo.kt`, `Sqrt.kt`, `HappyNumber.kt`, `PalindromeNumber.kt`, `MinimumMovesToEqualArrayElements.kt`, `PascalsTriangle.kt`, `StringtoIntegerAtoi.kt`, `CheckifaParenthesesStringCanBeValid.kt`, `MinimumDeletionToMakeCharacterFrequenciesUnique.kt`

### Sliding window ([ch15](ch15-sliding-window/pattern-primer.md))
`LongestSubArraysOfOneAfterDeletingOneElement.kt`, `MaximumErasureValue.kt`, `MaximumSumOfDistinctSubarraysWithLengthK.kt`, `MinimumSwapsToGroupAllOnesTogether.kt`, `MinimumNumberofSwapstoMaketheStringBalanced.kt`, `LongestContinuousSubarrayWithAbsoluteDifferenceLessThanOrEqualToLimit.kt`, `MaximumNumberofVowelsinSubstringofGivenLength.kt`, `MaximumLengthofaConcatenatedStringwithUniqueCharacters.kt`, `FindAllAnagrams.kt`, `MinimumWindowSubsequence.kt`, `PartitionLabels.kt`, `ProgrammerString.kt`, `LongestNiceSubarray.kt`

### Greedy ([ch11](ch11-greedy/pattern-primer.md))
`CanPlaceFlowers.kt`, `DestroyingAsteroids.kt`, `IncreasingTripletSequence.kt`, `MaximumSwap.kt`, `MinimumNumberOfTapsToWaterGarden.kt`, `MaxProfiAssigningWork.kt`, `MaximumDistanceInArray.kt`, `KItemsWithMaximumSum.kt`, `MinimumTimeToMakeRopeColorful.kt`, `MinimumReplacementToSortTheArray.kt`, `MaxChuncksToMakeSorted_II.kt`, `MaximumValueOfAnOrderedTriplet_II.kt`, `MInimumCostHomecomingOfARobot.kt`, `MinimumDeletionsToMakeStringBalanced.kt`, `MinimumOperationstoConvertAllElementstoZero.kt`

### Range-query structures (the appendix's "Range Query Warlords")
`SegmentTree.kt`, `IterativeSegmentTree.kt`, `DynamicSegmentTree.kt`, `FenwickTree.kt`, `CountOfSmallerNumberAfterSelf.kt`, `RangeSumQueryMutable.kt`, `RangeSumQuery2dMutable.kt`, `OrderedStatisticsTree.kt`, `OrderedStatisticsTreeForStreamers.kt`, `IntervalTree.kt`, `StreamerRanking.kt`, `FindingMKAverage.kt`, `DualBalancedHeap.kt`, `MyCalendar.kt`, `MyCalendar_II.kt`, `DesignANumberContainerSystem.kt`, `DesignFileSystem.kt`, `DesignHashMap.kt`, `DesignHitCounter.kt`, `NumberOfRecentCalls.kt`, `MovingAverageOfARunningStream.kt`, `ProductOfLastKNumbers.kt`, `DesignACircularQueue.kt`, `SnapshotArray.kt`, `RandomPickIndex.kt`, `LinkedListRandomNode.kt`, `DetectSquares.kt`, `DesignTicTacToe.kt`, `FindScoreOfAnArrayAfterMarkingAllElements.kt`, `LongestHappyString.kt`, `EqualRowAndColumnPairs.kt`

### Combinatorics & bits ([ch12](ch12-backtracking/pattern-primer.md)/[ch16](ch16-bit-manipulation/pattern-primer.md))
`Combinations.kt`, `Subsets_II.kt`, `Permutation_II_Backtracking.kt`, `Permutation_II_NarayanPandita.kt`, `PermutationHardFollowup.kt`, `NextGreaterElement_III.kt`, `NextPermutationShorter.kt`, `NQueen_II.kt`, `NQueenOptimized.kt`, `Strobogrammatic_Number_II.kt`, `ExpressionAndAddOperators.kt`, `ExpressionAndAddOperatorsOptimized.kt`, `PathWithMaximumGold.kt`, `LargestSquareAreaInMatrix.kt`, `MaxAreaOfIsland.kt`, `LongestNiceSubarray.kt`, `FirstLetterToAppearTwice.kt`

### Geometry / advanced math (the `math/geometry/` and `geo/` folders)
`ConvexHull.kt`, `ErectTheFence_ConvexHull.kt`, `MaxPointsOnALine.kt`, `CheckIfTwoLinesIntrsects.kt`, `CountNumberOfTrapizoids_I.kt`, `FindingNumberOfVisibleMountains.kt`, `SeperateSquares_I.kt`, `RectangleOverlap.kt`, `RectangleArea.kt`, `RectangleArea_II.kt`, `RectangleArea_II_SegmentTree.kt`, `ConstructQuadTree.kt`, `KDTreeExample.kt`, `QuadTreeUsagePlaceFinding.kt`

### Eulerian / SCC / flow extras ([17.9](ch17-advanced-graphs/reconstruct-itinerary.md)/[6.7](ch06-graphs/strongly-connected-components.md)/[17.1](ch17-advanced-graphs/max-flow-edmonds-karp.md))
`CrackingTheSafe.kt`, `FindEulerianCircuit.kt`, `ValidArrangementOfPairs.kt`, `ValidArrangementOfPairsRecursive.kt`, `Tarjans.kt`, `Kosaraju.kt`, `FindArticulationPoints.kt`, `CriticalConnectionsInANetworkShortCode.kt`, `EdmondsKarp.kt`, `EdmondsKarpAdjacencyList.kt`, `EdmondsKarpAnother.kt`, `EdmondsKarpImpovised.kt`, `BipartileMatching.kt`, `MaximumBipartileJobMatching.kt`, `TravellingSalespersonProblemBruteforceMatrix.kt`, `TravellingSalesPersonTopDownDP.kt`, `TravellingSalesmanRecursiveDP.kt`, `TravellingSalesmanDP` variants, `MaximumPathQualityOfAGraph.kt`

### The `google/`, `facebook/`, `microsoft/` folders
`ShuffleWithRandomness.kt`, `SongShuffle.kt` (now [11.10](ch11-greedy/reorganize-string.md)), `LargestSquareAreaInMatrix.kt`, `MInDifferenceBetweenTotalSums.kt`, `FindMinimumTicketPrice.kt`, `SecondGreatestNumber.kt`, `ValidTime.kt` (support)

## Priorities if you keep expanding

1. **`RegularExpressionMatching.kt` / `InterleavingString.kt` / `BurstBallonsClean.kt`** — the three "hardest classic DP" gaps.
2. **`BasicCalculator_III.kt`** — the full calculator stack machine.
3. **`SegmentTree.kt` + `FenwickTree.kt`** — the Range Query Warlords deserve their own chapter (the notes' appendix promised them).
4. **`CrackingTheSafe.kt` / `ValidArrangementOfPairs.kt`** — the Eulerian-family trio after [17.9](ch17-advanced-graphs/reconstruct-itinerary.md).
5. **`ConvexHull.kt` / `ErectTheFence`** — geometry is the least-covered corner of the repo.

> Every file listed here has its **source** in the repo and its **engine** documented in this book — the next edition's chapters will turn each line into a page.
