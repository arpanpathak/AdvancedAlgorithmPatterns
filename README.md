# Problem Solutions Repository

This repository contains solutions to various algorithmic problems, organized by categories such as arrays, backtracking, binary search, graphs, and more. Each problem is solved in Kotlin, and the solutions are structured in a way that makes it easy to navigate and understand.

## The Book

**Coding Interview Fight Club** — a from-scratch, multi-language book that explains this entire codebase. Every problem ships with intuition, multiple approaches, code in **five languages** (Kotlin, Java, C++, typed Python, Rust) annotated with `@param` / `@return` contracts, hand-traced dry runs, and math-backed time & space complexity analysis.

- **Read it online:** published to GitHub Pages from this repo (see the `mdbook.yml` workflow).
- **Build locally:** `cd CodingInterviewFightClub && mdbook build` (requires [mdBook](https://github.com/rust-lang/mdBook)).
- **Source of truth:** the Kotlin files under `src/main/kotlin/` — each book page links to its source file.
- **Contributing:** every page has an "Edit on GitHub" link in the toolbar.

## Purpose

The purpose of this repository is to provide a comprehensive collection of algorithmic problem solutions that can be used for learning, reference, or interview preparation. The problems are categorized by topic, and each solution is implemented in Kotlin.

## How to Generate an Updated File List

To generate an updated list of all files in this repository, you can use the following shell command:

```bash
find . -type f | sort > all_problems.txt
```

This command will recursively find all files in the repository, sort them, and save the list to a file named `all_problems.txt`.

## Repository Structure

Below is a tree-like structure of the repository with links to the files:
```
.
├── GenerateReadme.kt
├── Main.kt
├── README.md
├── all_files.txt
├── **array/**
│   ├── **Combinatorics/**
│   │   ├── ClosestSubsequenceSum.kt
│   │   ├── Combinations.kt
│   │   ├── NextGreaterElement_III.kt
│   │   ├── NextPermutation.kt
│   │   ├── Permutation_II.kt
│   │   ├── Permutation_II_Backtracking.kt
│   │   ├── Permutations.kt
│   │   └── Subsets.kt
│   ├── DiagonalTraverse.kt
│   ├── DiagonalTraverse_II.kt
│   ├── InsertInterval.kt
│   ├── MergeIntervals.kt
│   ├── MergeSortedArray.kt
│   ├── MissingRanges.kt
│   ├── MoveZeroes.kt
│   ├── RemoveElement.kt
│   ├── RotateImage.kt
│   ├── SearchA2dMatrix_II.kt
│   ├── SetMatrixZeroes.kt
│   ├── ShortestPathInBinaryMatrix.kt
│   ├── SignOfTheProductOfAnArray.kt
│   ├── SpiralMatrix.kt
│   ├── SpiralMatrix_II.kt
│   ├── ToeplitzMatrix.kt
│   ├── TransposeMatrix.kt
│   ├── **backtracking/**
│   │   ├── CombinationSum.kt
│   │   ├── CombinationSum3.kt
│   │   └── CombinationSum_II.kt
│   ├── **cycle/**
│   │   └── FindTheDuplicateNumber.kt
│   ├── **dfs/**
│   │   └── NestedListWeightedSum.kt
│   ├── **dp/**
│   │   ├── BurstBaloons.kt
│   │   ├── CoinChange.kt
│   │   ├── CoinChange_II.kt
│   │   ├── CoinChange_II_BottomUp.kt
│   │   ├── HouseRobber.kt
│   │   ├── HouseRobber_II.kt
│   │   ├── KadensAlgorithm.kt
│   │   ├── LongestCommonSubarray.kt
│   │   ├── LongestIncreasingSequenceInAMatrix.kt
│   │   ├── LongestIncreasingSubsequence.kt
│   │   ├── MaximalSquare.kt
│   │   ├── MaximumSumOfNonAdjacentElements.kt
│   │   ├── MaximumSumSubArray.kt
│   │   ├── MinCostClimbingStaris.kt
│   │   ├── MinimumNumberofIncrementsSubarraysFormaTargetArray.kt
│   │   ├── MinimumPathSum.kt
│   │   ├── PartitionArrayIntoTwoArrayToMinimuzeSumDifference.kt
│   │   ├── SplitArrayLargestSum.kt
│   │   └── TargetSum.kt
│   ├── **greedy/**
│   │   ├── CanPlaceFlowers.kt
│   │   ├── ContainerWithMostWater.kt
│   │   ├── IncreasingTripletSequence.kt
│   │   ├── KItemsWithMaximumSum.kt
│   │   ├── MaximumDistanceInArray.kt
│   │   ├── MaximumSwap.kt
│   │   ├── MergeOverlappingIntervals.kt
│   │   ├── MinimumNumberofSwapstoMaketheStringBalanced.kt
│   │   └── NonOverlappingIntervals.kt
│   ├── **hashtable/**
│   │   ├── ContainsDuplicate_II.kt
│   │   ├── DivideArrayIntoEqualPairs.kt
│   │   ├── EqualRowAndColumnPairs.kt
│   │   ├── FindDifferenceOfTwoArrays.kt
│   │   ├── FindMissingPositive.kt
│   │   ├── FirstMissingPositive.kt
│   │   ├── IntegerToEnglishWords.kt
│   │   ├── LongestConsecutiveSequence.kt
│   │   ├── MaxNUmWithKSumPairs.kt
│   │   ├── RankTransformOfAnArray.kt
│   │   ├── SetMismatch.kt
│   │   ├── SnapshotArray.kt
│   │   ├── UniqueNumberOfOccurences.kt
│   │   └── ValidSudoku.kt
│   ├── **prefixsum/**
│   │   ├── ContiguousArray.kt
│   │   ├── ContinuousSubarraySum.kt
│   │   ├── FIndTheHighestAltitute.kt
│   │   ├── FindPivotIndex.kt
│   │   ├── Minimum NumberofOperationstoMoveAllBallstoEachBox.kt
│   │   ├── NumberOfZeroFilledSubArrays.kt
│   │   ├── ProductOfArrayExceptSelf.kt
│   │   ├── SubArrayProductLessThanK.kt
│   │   ├── SubArraySumEqualsToK.kt
│   │   ├── SubArraySumsDivisibleByK.kt
│   │   └── ZeroArrayTransformation_I.kt
│   ├── **random/**
│   │   └── RandomPickIndex.kt
│   ├── **sorting/**
│   │   ├── SortColors.kt
│   │   └── SquaresOfASortedArray.kt
│   ├── **sweepline/**
│   │   └── MaximumPopulationYear.kt
│   └── **twopointer/**
│       ├── 4Sum.kt
│       ├── IntervalListIntersection.kt
│       ├── RemoveDuplicateElementsFromSortedArray.kt
│       ├── RemoveDuplicateElementsFromSortedArray_II.kt
│       ├── RotateArray.kt
│       ├── ThreeSum.kt
│       ├── ThreeSumClosest.kt
│       ├── TrappingRainWater.kt
│       └── TwoSum_II.kt
├── **automata/**
│   └── **regular_language/**
├── **autopilot/**
│   └── H1bAutoPilotStressAnxietyAlgorithm.kt
├── **backtracking/**
│   ├── ExpressionAndAddOperators.kt
│   ├── ExpressionAndAddOperatorsOptimized.kt
│   ├── NQueen.kt
│   ├── NQueen_II.kt
│   ├── PalindromePartitioning.kt
│   ├── PartitionToKEqualSumSubsets.kt
│   ├── RestoreIPAddresses.kt
│   ├── Strobogrammatic_Number_II.kt
│   ├── SudokuSolver.kt
│   └── SudokuSolverSet.kt
├── **binarysearch/**
│   ├── ApartmentHunting.kt
│   ├── CapacityToShipPackageWithinDDays.kt
│   ├── ClosestSebsequenceSum.kt
│   ├── FindFirstAndLastPosition.kt
│   ├── FindKClosestElements.kt
│   ├── FindMinimumInRotatedSortedArray.kt
│   ├── FindPeakElement.kt
│   ├── FindPeakElementBetterSolution.kt
│   ├── FirstBadVersion.kt
│   ├── GuessNumberHigherOrLower.kt
│   ├── HouseRobber_IV.kt
│   ├── KThMissingPositiveNumber.kt
│   ├── KokoEatingBanana.kt
│   ├── MedianOfTwoSortedARrays.kt
│   ├── PeakIndexInMountainArray.kt
│   ├── RandomPickWithWeight.kt
│   ├── SearchA2dMatrix.kt
│   ├── SearchInRotatedArray_II.kt
│   ├── SearchInRotatedSortedArray.kt
│   ├── SearchInsertionPosition.kt
│   ├── SingleElementInASortedArray.kt
│   └── ValleyElement.kt
├── **bitset/**
│   ├── FirstLetterToAppearTwice.kt
│   ├── LongestNiceSubarray.kt
│   ├── MaximumXorOfTwoNumsInArray.kt
│   ├── Number of Steps to ReduceaANumberInBinaryRepresentationtoOne.kt
│   ├── NumberOfOneBits.kt
│   ├── ReverseBits.kt
│   ├── SingleNumber.kt
│   ├── SingleNumber3.kt
│   ├── SmallestNumberWithAllSetBits.kt
│   └── SumOfAllSubsetXorTotal.kt
├── **branch_and_bound/**
├── **cache/**
│   ├── LFUCache.kt
│   ├── LRUCache.kt
│   ├── LRUCacheBetter.kt
│   ├── LRUCacheLinkedList.kt
│   ├── LRUCleanAf.kt
│   └── LruCacheBruceLee.kt
├── **combinatorics/**
├── **commons/**
│   ├── APIEndPoints.kt
│   ├── AlpacaWebSocketFactory.kt
│   └── FileWriter.kt
├── **disjointset/**
│   ├── AccountMerge.kt
│   ├── NumberOfIsland_II.kt
│   ├── NumerOfIsland_II_Optimized.kt
│   └── UnionFind.kt
├── **divide_and_conquer/**
├── **dynamic_programming/**
│   ├── ClosestSubsequenceSum.kt
│   ├── FrogJump.kt
│   ├── MaximumProductSubarray.kt
│   ├── MaximumProfitInJobScheduling.kt
│   ├── PartitionEqualSubsetSum.kt
│   └── SuperEggDropping.kt
├── **facebook/**
│   ├── FindMinimumTicketPrice.kt
│   └── SecondGreatestNumber.kt
├── **geo/**
│   ├── **kdtree/**
│   │   └── KDTreeExample.kt
│   └── **quadtree/**
│       ├── ConstructQuadTree.kt
│       ├── QuadTree.kt
│       └── QuadTreeUsagePlaceFinding.kt
├── **google/**
│   └── LargestSquareAreaInMatrix.kt
├── **graph/**
│   ├── BusRoutes.kt
│   ├── CalculateGraphDiameter.kt
│   ├── ChromaticNumber.kt
│   ├── CloneGraph.kt
│   ├── DiameterOfBinaryTree.kt
│   ├── HouseRobber3.kt
│   ├── IsBipartileGraph.kt
│   ├── IsBipartileGraphDfs.kt
│   ├── MaximumPathQualityOfAGraph.kt
│   ├── MinimumGeneticMutations.kt
│   ├── NColoringGraph.kt
│   ├── NColoringGreedy.kt
│   ├── ReorderRoutesToMakeAllPathsLeadToCityZero.kt
│   ├── WordLadder.kt
│   ├── WordLadder_II.kt
│   ├── WordLadder_II_clean.kt
│   ├── **articulation_point/**
│   │   ├── CriticalConnectionsInANetwork.kt
│   │   ├── CriticalConnectionsInANetworkShortCode.kt
│   │   └── FindArticulationPoints.kt
│   ├── **bst/**
│   │   ├── BinarySearchTreeToGreaterSumTree.kt
│   │   └── RangeSumOfBST.kt
│   ├── **components/**
│   │   └── FindConnectedComponents.kt
│   ├── **cycle/**
│   │   ├── CourseSchedule.kt
│   │   ├── CourseSchedule_II.kt
│   │   └── ParallelCourses.kt
│   ├── **dag/**
│   ├── **dp/**
│   │   ├── BellmanFordAlgorithm.kt
│   │   ├── CheapestFlightsWithinKStops.kt
│   │   ├── CheapestFlightsWithinKStopsBellman.kt
│   │   └── FloydWarshallAlgorithm.kt
│   ├── **euler/**
│   │   └── **circuit/**
│   │       ├── CrackingTheSafe.kt
│   │       ├── EulerianGraph.kt
│   │       └── **path/**
│   │           └── ReconstructItenary.kt
│   ├── **greedy/**
│   │   ├── CheapestFlightWithKStops.kt
│   │   ├── CheapestFlightsWithKStops.kt
│   │   ├── DonaldTrumpAlgorithm.kt
│   │   └── TheMaze_III.kt
│   ├── **mst/**
│   │   ├── FindRedundentConnections.kt
│   │   ├── OptimizeWaterDistributionInAVillage.kt
│   │   ├── PrimsAlgorithm.kt
│   │   └── PrimsShorter.kt
│   ├── **scc/**
│   │   ├── Kosaraju.kt
│   │   └── Tarjans.kt
│   ├── **topological_sort/**
│   │   ├── AlienDictionary.kt
│   │   ├── AlienDictionary_BFS.kt
│   │   ├── ApplySubstitutions.kt
│   │   ├── CourseSchedule_II.kt
│   │   └── CourseSchedule_II_BFS.kt
│   └── **tsp/**
│       ├── ShortestPathVisitingAllNodes.kt
│       ├── TSPHelpKarp.kt
│       └── TravellingSalespersonProblemBruteforceMatrix.kt
├── **greedy/**
│   ├── CarFleet.kt
│   ├── DestroyingAsteroids.kt
│   ├── JumpGame.kt
│   ├── JumpGame_II.kt
│   ├── MInimumCostHomecomingOfARobot.kt
│   ├── MaxChuncksToMakeSorted_II.kt
│   ├── MaxProfiAssigningWork.kt
│   ├── MaximumValueOfAnOrderedTriplet_II.kt
│   ├── MeetingRooms.kt
│   ├── MeetingRooms_II.kt
│   ├── MeetingRooms_II_greedy.kt
│   ├── MinimumDeletionsToMakeStringBalanced.kt
│   ├── MinimumReplacementToSortTheArray.kt
│   ├── MinimumTimeToMakeRopeColorful.kt
│   ├── RescheduleMeetingsforMaximumFreeTime_I.kt
│   └── TaskScheduler.kt
├── **grid/**
│   ├── FloodFill.kt
│   ├── IslandPerimeter.kt
│   ├── MakingALargeIsland.kt
│   ├── MakingALargeIsland_AnotherApproach.kt
│   ├── MaxAreaOfIsland.kt
│   ├── MaximumNumberOfFishInAGrid.kt
│   ├── PacificAtlanticWaterFlow.kt
│   ├── RottingOranges.kt
│   ├── ShortestBridge.kt
│   ├── ShortestDistanceFromAllBuildings.kt
│   ├── WallsAndGates.kt
│   ├── **a_star/**
│   │   └── ShortestPathInGridWithObstaclesElimination.kt
│   ├── **dynamic_programming/**
│   │   ├── CherryPickup.kt
│   │   ├── Test.kt
│   │   ├── UniquePaths_I.kt
│   │   └── UniquePaths_II.kt
│   ├── **histogram/**
│   │   └── MaximalRectangle.kt
│   └── **search/**
│       └── WordSearch_II.kt
├── **hashtable/**
│   ├── CountNumberOfBadPairs.kt
│   ├── DesignANumberContainerSystem.kt
│   ├── DesignFileSystem.kt
│   ├── DesignHashMap.kt
│   ├── FirstUniqueCharacter.kt
│   ├── HIndex.kt
│   ├── IntegerToRoman.kt
│   ├── IntersectionOfTwoArray.kt
│   ├── MaximumFrequencyStack.kt
│   ├── RomanToInteger.kt
│   ├── WorkBreak_II.kt
│   └── WorlBreak_I_DP.kt
├── **heap/**
│   ├── DualBalancedHeap.kt
│   ├── FindKClosestElements.kt
│   ├── FindScoreOfAnArrayAfterMarkingAllElements.kt
│   ├── FindingMKAverage.kt
│   ├── IPO.kt
│   ├── LongestHappyString.kt
│   ├── MedianFromRunningStream.kt
│   ├── MeetingRoom_III.kt
│   ├── SingleThreadedCPU.kt
│   ├── SlidingWindowMedian.kt
│   └── TopKFrequentElements.kt
├── **linkedlist/**
│   ├── AddTwoNumbers.kt
│   ├── CopyLinkedListWithRandomPointer.kt
│   ├── DeleteMiddleNodeOfLinkedList.kt
│   ├── InsertIntoASortedCircularLinkedList.kt
│   ├── InsertIntoASortedCircularList.kt
│   ├── IntersectionOfTwoLinkedList.kt
│   ├── LinkedListCycle.kt
│   ├── LinkedListCycle_II.kt
│   ├── MaximumTwinSumOfALinkedList.kt
│   ├── MergeKSortedList.kt
│   ├── MergeKSortedListIterative.kt
│   ├── MergeTwoSortedLIst.kt
│   ├── MiddleNode.kt
│   ├── OddEvenLinkedList.kt
│   ├── OddOrEvenLinkedList.kt
│   ├── PalindromeLinkedList.kt
│   ├── RemoveNthNodeFromEndOfList.kt
│   ├── ReverseLinkedList.kt
│   ├── ReverseLinkedListIterative.kt
│   ├── ReverseNodesInKGroups.kt
│   ├── RotateList.kt
│   └── SwapNodesInPairs.kt
├── **math/**
│   ├── AddStrings.kt
│   ├── DesignTicTacToe.kt
│   ├── DetectSquares.kt
│   ├── DivideTwoIntegers.kt
│   ├── HappyNumber.kt
│   ├── MinimumMovesToEqualArrayElements.kt
│   ├── MultiplyStrings.kt
│   ├── PlusOne.kt
│   ├── PowerOfTwo.kt
│   ├── ReverseInteger.kt
│   ├── SlidingPuzzle.kt
│   ├── Sqrt.kt
│   ├── StringtoIntegerAtoi.kt
│   ├── **binary/**
│   │   └── AddBinary.kt
│   ├── **dp/**
│   │   └── PascalsTriangle.kt
│   ├── **geometry/**
│   │   ├── ConvexHull.kt
│   │   ├── CountNumberOfTrapizoids_I.kt
│   │   ├── ErectTheFence_ConvexHull.kt
│   │   ├── HowManyRectanglesOverlapSweepLine.kt
│   │   ├── HowManyRectanglesOverlaping.kt
│   │   ├── MaxPointsOnALine.kt
│   │   ├── RectangleArea.kt
│   │   ├── RectangleOverlap.kt
│   │   ├── **binarysearch/**
│   │   │   └── SeperateSquares_I.kt
│   │   └── **interval/**
│   │       ├── HowManyRectangleOverlapsIntervalTree.kt
│   │       └── RectangeOverlapCountTreeSet.kt
│   ├── pow.kt
│   └── **stack/**
│       ├── BasicCalculator.kt
│       ├── BasicCalculator_I.kt
│       ├── BasicCalculator_II.kt
│       ├── BasicCalculator_III.kt
│       └── BasicCalculator_II_ShortCode.kt
├── **microsoft/**
│   ├── Demo.kt
│   ├── NonNegativeSum.java
│   ├── Toast.kt
│   └── ValidTime.kt
├── **ml/**
│   └── **classical/**
│       └── **tree/**
│           ├── **core/**
│           │   └── DecisionTree.kt
│           └── **ensemble/**
├── **nlp/**
├── **numbers/**
│   └── PalindromeNumber.kt
├── **probability/**
│   ├── InsertDeleteGetRandom.kt
│   ├── InsertDeleteGetRandomAtO1.kt
│   ├── LinkedListRandomNode.kt
│   ├── PathWithMaximumProbability.kt
│   └── ReservoirSampling.kt
├── **queues/**
│   └── **dequeue/**
├── **queueu/**
│   └── **dequeue/**
│       ├── DesignACircularQueue.kt
│       ├── DesignHitCounter.kt
│       ├── NumberOfRecentCalls.kt
│       └── ProductOfLastKNumbers.kt
├── **quicksort/**
│   ├── KClosestPointsToOrigin.kt
│   ├── KThLargestElementInArray.kt
│   ├── KthLargestElementInArrayTailRec.kt
│   └── TopKFrequentElements.kt
├── **real_word_projects/**
│   ├── HttpApiCall.kt
│   ├── InterfaceExample.kt
│   ├── ParallelFibonacci.kt
│   ├── ScaleTransactions.kt
│   ├── SystemInterviewHack.kt
│   ├── TradingAPICallExample.kt
│   ├── **advertisement/**
│   │   └── DataModels.kt
│   ├── **consistent_hashing/**
│   │   └── ConsistentHashing.kt
│   ├── **database/**
│   │   └── AiTest.kt
│   ├── **dynamic_programming/**
│   │   └── DuckworthLewisStern.kt
│   ├── **json/**
│   │   └── JsonExample.kt
│   ├── **parallel_algorithms/**
│   │   ├── FindMaxInArray.kt
│   │   └── ParallelMatrixMultiplication.kt
│   └── **trading/**
│       ├── AlpacaSDKExamples.kt
│       ├── RealtimeMarketDataStreaming.kt
│       ├── **stream/**
│       ├── system_design_plantuml.txt
│       └── trading_data.json
├── **simulation/**
│   ├── CarPooling.kt
│   ├── CountCollisionsOnARoad.kt
│   ├── FindWinnerOnATicTacToeGame.kt
│   ├── Racecar.kt
│   ├── RobotBoundedInCircle.kt
│   └── TextJustification.kt
├── **sliding_window/**
│   ├── LongestContinuousSubarrayWithAbsoluteDifferenceLessThanOrEqualToLimit.kt
│   ├── LongestRepeatingCharacterReplacement.kt
│   ├── LongestSubArraysOfOneAfterDeletingOneElement.kt
│   ├── LongestSubstringWithoutRepeatingCharacter.kt
│   ├── MaxConsecutiveOnes_III.kt
│   ├── MaximumAverageSubarray_I.kt
│   ├── MaximumErasureValue.kt
│   ├── MaximumSumOfDistinctSubarraysWithLengthK.kt
│   ├── MinimumSizeSubarraySum.kt
│   ├── MinimumSwapsToGroupAllOnesTogether.kt
│   ├── PartitionLabels.kt
│   ├── ProgrammerString.kt
│   └── SlidingWindowMaximum.kt
├── **sorting/**
│   ├── EmployeeFreeTime.kt
│   ├── HIndex.kt
│   ├── LargestNumber.kt
│   ├── MergeSort.kt
│   ├── RankTeamsByVote.kt
│   └── RussianDollEnvelope.kt
├── **speed_dating/**
│   └── ComputerScienceEngineerDating.kt
├── **stack/**
│   ├── AestroidCollisions.kt
│   ├── BuildingsWithAnOceanView.kt
│   ├── DailyTemperatures.kt
│   ├── DesignAStackWithIncrementOperations.kt
│   ├── EvaluateReversePolishNotation.kt
│   ├── ExclusiveTimeOfFunctions.kt
│   ├── FlattenNestedListIterator.kt
│   ├── LargestRectangleInHistogram.kt
│   ├── LongestValidParanthesis.kt
│   ├── MinStack.kt
│   ├── MinimumAddtoMakeParenthesesValid.kt
│   ├── MinimumDeletionsToMakeStringBalanced.kt
│   ├── MinimumRemoveToMakeValidParentheses.kt
│   ├── NextGreaterElement_I.kt
│   ├── NextGreaterElement_II.kt
│   ├── NumberOfVisiblePeopleInAQueue.kt
│   ├── OneThreeTwoPattern.kt
│   ├── OnlineStockSpan.kt
│   ├── RemoveDuplicateLetters.kt
│   ├── RemoveStarsFromString.kt
│   ├── SmallestSubsequenceOfDistinctCharacters.kt
│   ├── SumOfSubArrayMinimum.kt
│   ├── SumOfSubArrayRanges.kt
│   └── ValidParentheses.kt
├── **stock_market/**
│   ├── **dp/**
│   │   ├── BestTimeToBuyAndSellStock.kt
│   │   ├── BestTimeToBuyAndSellStockWithCooldown.kt
│   │   ├── BestTimeToBuyAndSellStockWithTransactionFee.kt
│   │   └── BestTimeToBuyAndSellStock_III.kt
│   └── **greedy/**
│       └── BestTimeToBuyAndSellStock_II.kt
├── **stream/**
│   └── MovingAverageOfARunningStream.kt
├── **string/**
│   ├── ApplySubstitutions.kt
│   ├── CheckifaParenthesesStringCanBeValid.kt
│   ├── CountAndSay.kt
│   ├── CountNumberOfWordsWhichAreSubSequence.kt
│   ├── CountWordsWithAGivenPrefix.kt
│   ├── ExcelSheetToColumnNumber.kt
│   ├── FindUniqueBinaryString.kt
│   ├── GoatLatin.kt
│   ├── GreatestCommonDivisorOfStrings.kt
│   ├── GroupAnagrams.kt
│   ├── IsSubsequence.kt
│   ├── IsomorphicString.kt
│   ├── LengthOfLastWord.kt
│   ├── LongestCommonPrefix.kt
│   ├── LongestPalidnromicSubstring.kt
│   ├── MaximumLengthofaConcatenatedStringwithUniqueCharacters.kt
│   ├── MaximumValueOfAStringIsAnArray.kt
│   ├── MergeStringAlternatively.kt
│   ├── MinimumDeletionToMakeCharacterFrequenciesUnique.kt
│   ├── ReverseVowelOfString.kt
│   ├── ReverseWordsInString.kt
│   ├── StringCompression.kt
│   ├── StringCompression_II.kt
│   ├── ValidAnagram.kt
│   ├── ValidNumber.kt
│   ├── ValidPalindrome.kt
│   ├── ValidPalindrome_II.kt
│   ├── ValidWordAbbreviation.kt
│   ├── ValidateIPAddress.kt
│   ├── **backtracking/**
│   │   └── GenerateParantheses.kt
│   ├── **dynamic_programming/**
│   │   ├── DeleteOperationsForTwoStrings.kt
│   │   ├── EditDistance.kt
│   │   ├── InterleavingString.kt
│   │   ├── LongestCommonSubsequence.kt
│   │   ├── LongestCommonSubstring.kt
│   │   ├── LongestPalindromicSubsequence.kt
│   │   ├── LongestPalindromicSubsequence_BottomUp.kt
│   │   ├── LongestStringChain.kt
│   │   ├── RegularExpressionMatching.kt
│   │   ├── ShortestCommonSupersequence.kt
│   │   ├── ValidPalindrome_III.kt
│   │   └── ValidPalindrome_III_SpaceOptimized.kt
│   ├── **greedy/**
│   │   ├── BreakAPalindrome.kt
│   │   └── ShortestWayToFormAString.kt
│   ├── **hashtable/**
│   │   ├── DetermineIfStringsAreClose.kt
│   │   ├── GroupShiftedStrings.kt
│   │   ├── PermutationsInString.kt
│   │   ├── UniqueLength3PalindromicSubsequence.kt
│   │   └── UniqueSubstringWithEqualDigitFrequency.kt
│   ├── **pattern_matching/**
│   │   ├── FindTheIndexofTheFirstOccurrenceIna String.kt
│   │   └── FindTheIndexofTheFirstOccurrenceIna String_RabinKarp.kt
│   ├── **sliding_window/**
│   │   ├── FindAllAnagrams.kt
│   │   ├── MaximumNumberofVowelsinSubstringofGivenLength.kt
│   │   └── MinimumWindowSubstring.kt
│   ├── **sorting/**
│   │   ├── CustomSortString.kt
│   │   └── CustomSortString_Linear.kt
│   ├── **stack/**
│   │   ├── DecodeString.kt
│   │   ├── RemoveAllAdjacentDuplicatesInString.kt
│   │   └── SimplifyPath.kt
│   └── **travelling_salesman/**
├── **trading/**
│   └── trading_data.json
├── **tree/**
│   ├── AllNodesDistanceKinBinaryTree.kt
│   ├── BInaryTreeInOrderTraversalIterative.kt
│   ├── BalancedBinaryTree.kt
│   ├── BinaryTreeLevelOrderTraversal.kt
│   ├── BinaryTreeMaximumPathSum.kt
│   ├── BinaryTreeRightSideView.kt
│   ├── BinaryTreeVerticalOrderTraversal.kt
│   ├── BinaryTreeVerticalOrderTraversal_WithoutSorting.kt
│   ├── BinaryTreeZigZagLevelOrderTraversal.kt
│   ├── BoundaryOfBinaryTree.kt
│   ├── ConstructBinaryTreeFromInorderAndPostOrderTraversal.kt
│   ├── ConstructBinaryTreeFromPreorderAndInOrderTraversal.kt
│   ├── ConstructBinaryTreeFromString.kt
│   ├── CountGoodNodeInBInaryTree.kt
│   ├── CountNodeEqualsAverage.kt
│   ├── DiameterOfNArrayTree.kt
│   ├── LeafSimilar.kt
│   ├── LongestPathWithDifferentAdjacentCharacters.kt
│   ├── LongestUnivaluePath.kt
│   ├── LowestCommonAncestor.kt
│   ├── LowestCommonAncestor_III.kt
│   ├── MaximumDepthOfBinaryTree.kt
│   ├── MaximumSumBSTInBinaryTree.kt
│   ├── MaximumWidthOfBinaryTree.kt
│   ├── MinimumTimeToCollectAllApplesInATree.kt
│   ├── PathSum.kt
│   ├── PathSumIII.kt
│   ├── PathSum_II.kt
│   ├── PopulateNextRightPointersInEachNode_II.kt
│   ├── PopulateNextRightPointersInEachNode_II_Constant.kt
│   ├── PopulatingNextRightPointerInEachNode.kt
│   ├── RecoverATreeFromPreOrderTraversal.kt
│   ├── SerializeAndDeserializeABinaryTree.kt
│   ├── SerializeAndDeserializeNArrayTree.kt
│   ├── SlidingWindowMedianTreeSet.kt
│   ├── StepByStepDirectionsFromANodeToAnother.kt
│   ├── SumRootToLeafNumbers.kt
│   ├── VerticalOrderTraversalOfABinaryTree.kt
│   ├── **bfs/**
│   │   ├── AverageOfLevelsInBinaryTree.kt
│   │   ├── BinaryTreeLevelOrderTraversal_II.kt
│   │   ├── CheckCompletenessOfBinaryTree.kt
│   │   └── FindLargestValueInEachTreeRow.kt
│   ├── **bst/**
│   │   ├── BSTIterator.kt
│   │   ├── ClosestBinarySearchTreeValue.kt
│   │   ├── ConvertBInarySearchTreeToSortedDoublyLinkedList.kt
│   │   ├── DeleteNodeinABST.kt
│   │   ├── InorderSuccessor.kt
│   │   ├── LongestIncreasingSubsequence.kt
│   │   ├── RecoverBinarySearchTree.kt
│   │   ├── SkylineProblem.kt
│   │   ├── UniqueBinarySearchTrees.kt
│   │   └── UniqueBinarySearchTrees_II.kt
│   ├── **fenwick/**
│   │   ├── CountOfSmallerNumberAfterSelf.kt
│   │   ├── FenwickTree.kt
│   │   ├── RangeSumQuery2dMutable.kt
│   │   └── RangeSumQueryMutable.kt
│   ├── **interval/**
│   │   └── IntervalTree.kt
│   ├── **mst/**
│   │   ├── MinCostToConnectAllPointsKruskal.kt
│   │   └── MinCostToConnectAllPointsPrims.kt
│   └── **segment/**
│       └── SegmentTree.kt
└── **trie/**
    ├── AutoCompleteSystem.kt
    ├── AutoCompleteSystemWithHeap.kt
    ├── CountWordsWithAGivenPrefix_Trie.kt
    ├── CountWordsWithAGivenPrefix_Trie_FP.kt
    ├── DesignAddAndSearchWordDataStructure.kt
    ├── EqualRowAndColumnPairs.kt
    ├── LongestCommonPrefix.kt
    ├── SearchSuggestionSystem.kt
    └── WordBreak_I.kt
```

## Contributing

If you would like to contribute to this repository, feel free to submit a pull request. Please ensure that your solutions are well-documented and follow the existing structure.

## License

This repository is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.

---

Happy coding! 🚀